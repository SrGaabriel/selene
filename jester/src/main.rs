use std::fs;
use std::path::{Path, PathBuf};
use std::process::{Command, Stdio};

fn main() {
    let binding = std::env::current_dir().expect("Failed to get current directory");
    let project_root = binding.parent().expect("Failed to get parent directory");
    let gwydion_jar = project_root.join("build/libs/gwydion.jar");

    if !gwydion_jar.exists() {
        return;
    }

    let stdlib = project_root.join("stdlib");
    compile_project_sources(&stdlib, project_root, true);
    let bard_dir = project_root.join("bard");
    compile_project_sources(&bard_dir, project_root, false);

    link_files(project_root);
}

fn compile_project_sources(source_root: &Path, project_root: &Path, is_stdlib: bool) {
    let target_srcs = source_root.join("src");

    if let Ok(entries) = fs::read_dir(&target_srcs) {
        for entry in entries.flatten() {
            let path = entry.path();
            if path.extension().map_or(false, |ext| ext == "wy") {
                compile(&path, is_stdlib, project_root);
            }
        }
    }
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
    command.arg("-o").arg(project_root.join(format!("bard/output/{}", output_filename)));
    command.args(&files);
    command.current_dir(&output_dir);
    command.stdout(Stdio::inherit());
    command.stderr(Stdio::inherit());

    let status = command.status().expect("Failed to execute clang");
    if !status.success() {
        eprintln!("Clang failed with status: {}", status);
    }
}

fn compile(file: &Path, is_stdlib: bool, project_root: &Path) {
    println!("Compiling {:?}", file);
    let gwydion_jar = project_root.join("build/libs/gwydion.jar");

    let output_dir = project_root.join("bard/output/ll");
    fs::create_dir_all(&output_dir).expect("Failed to create output directory");

    let mut command = Command::new("java");
    command.arg("-jar")
        .arg(&gwydion_jar)
        .arg(file)
        .current_dir(&output_dir);

    if is_stdlib {
        command.arg("--internal-compile-stdlib");
    }

    let output = command.output().expect("Failed to execute java process");
    output.stdout.iter().for_each(|b| print!("{}", *b as char));
    output.stderr.iter().for_each(|b| eprint!("{}", *b as char));
}