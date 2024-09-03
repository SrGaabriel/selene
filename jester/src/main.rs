use serde::Deserialize;
use std::{fs, thread};
use std::path::{Display, Path, PathBuf};
use std::process::{Command, Stdio};
use std::time::Duration;

fn main() {
    let args = std::env::args().collect::<Vec<String>>();
    let native = args.contains(&String::from("--native"));
    if native {
        println!("Building native executable");
    }

    let binding = std::env::current_dir().expect("Failed to get current directory");
    let project_root = binding.parent().expect("Failed to get parent directory");
    let gwydion_jar = project_root.join("compiler/build/libs/gwydion.jar");

    if !gwydion_jar.exists() {
        return;
    }

    // delete output folder (current directory / output)
    let output_dir = binding.join("output");
    if output_dir.exists() {
        remove_dir_all_retry(&output_dir, 3, 100).expect("Failed to remove output directory");
    } else {
        println!("Output directory does not exist");
    }

    let bard_dir = project_root.join("bard");
    let stdlib = project_root.join("stdlib");
    let stdlib_props = parse_properties(&stdlib);
    let stdlib_result = compile(
        &stdlib_props.name,
        &stdlib,
        &project_root,
        &gwydion_jar,
        native,
        true,
    );
    if !stdlib_result {
        eprintln!("Failed to compile stdlib");
        return;
    }

    let bard_props = parse_properties(&bard_dir);
    let bard_result = compile(
        &bard_props.name,
        &bard_dir,
        &project_root,
        &gwydion_jar,
        native,
        false,
    );
    if !bard_result {
        eprintln!("Failed to compile project");
        return;
    }

    link_files(&project_root);
}

fn link_files(project_root: &Path) {
    let output_dir = project_root.join("bard/output/ll");

    let files: Vec<PathBuf> = fs::read_dir(&output_dir)
        .unwrap_or_else(|_| panic!("Failed to read directory: {:?}", output_dir))
        .filter_map(|entry| entry.ok().map(|e| e.path()))
        .filter(|path| path.extension().map_or(false, |ext| ext == "ll"))
        .collect();

    if files.is_empty() {
        return;
    }

    let mut command = Command::new("clang");
    let output_filename = if cfg!(windows) {
        "output.exe"
    } else {
        "output"
    };
    command
        .arg("-o")
        .arg(project_root.join(format!("bard/output/{}", output_filename)));
    command.args(&files);

    if !cfg!(windows) {
        command.arg("-lm");
    }

    command.current_dir(&output_dir);
    command.stdout(Stdio::inherit());
    command.stderr(Stdio::inherit());

    let status = command.status().expect("Failed to execute clang");
    if !status.success() {
        eprintln!("Clang failed with status: {}", status);
    }
}

fn compile(
    name: &String,
    target_root: &Path,
    gwydion_root: &Path,
    gwydion_jar: &Path,
    native: bool,
    is_stdlib: bool
) -> bool {
    println!("Compiling {:?}", target_root);

    let output_dir = gwydion_root.join("bard/output/ll");
    fs::create_dir_all(&output_dir).expect("Failed to create output directory");

    if !native {
        let mut command = Command::new("java");
        command.arg("-jar")
            .arg(&gwydion_jar)
            .arg(target_root)
            .arg(name)
            .current_dir(&output_dir);

        if is_stdlib {
            command.arg("--internal-stdlib");
        }

        let output = command.output().expect("Failed to execute java process");
        output.stdout.iter().for_each(|b| print!("{}", *b as char));
        output.stderr.iter().for_each(|b| eprint!("{}", *b as char));

        output.status.success()
    } else {
        let mut command = Command::new("./compiler");
        command.arg(target_root)
            .arg(name)
            .current_dir(&output_dir);

        if is_stdlib {
            command.arg("--internal-stdlib");
        }

        let output = command.output().expect("Failed to execute native binary");
        output.stdout.iter().for_each(|b| print!("{}", *b as char));
        output.stderr.iter().for_each(|b| eprint!("{}", *b as char));

        output.status.success()
    }
}

#[derive(Deserialize)]
pub struct ModuleProperties{
    name: String
}

fn parse_properties(module_root: &Path) -> ModuleProperties {
    let toml_path = module_root.join("module.toml");
    let toml_str = fs::read_to_string(&toml_path).expect("Failed to read module.toml");
    toml::from_str(&toml_str).expect("Failed to parse module.toml")
}

fn remove_dir_all_retry<P: AsRef<Path>>(path: P, retries: u32, delay_ms: u64) -> std::io::Result<()> {
    for _ in 0..retries {
        match fs::remove_dir_all(&path) {
            Ok(_) => {
                if !path.as_ref().exists() {
                    return Ok(());
                }
            }
            Err(err) => {
                // Handle the specific error if needed
                eprintln!("Failed to remove directory: {}", err);
            }
        }

        // Wait for the specified duration before retrying
        thread::sleep(Duration::from_millis(delay_ms));
    }

    // Final attempt outside of the loop
    fs::remove_dir_all(&path)?;
    if path.as_ref().exists() {
        Err(std::io::Error::new(std::io::ErrorKind::Other, "Directory still exists after maximum retries"))
    } else {
        Ok(())
    }
}