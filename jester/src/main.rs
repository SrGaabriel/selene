use serde::Deserialize;
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

    let bard_dir = project_root.join("bard");
    let stdlib = project_root.join("stdlib");
    let stdlib_props = parse_properties(&stdlib);
    compile_project_sources(&stdlib_props, &stdlib, project_root, true);
    let bard_props = parse_properties(&bard_dir);
    compile_project_sources(&bard_props, &bard_dir, project_root, false);

    link_files(project_root);
}

fn compile_project_sources(props: &ModuleProperties, source_root: &Path, project_root: &Path, is_stdlib: bool) {
    compile(&props.name, &source_root, is_stdlib, project_root);
}

fn link_files(project_root: &Path) {
    let output_dir = project_root.join("bard\\output\\ll");

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

fn compile(name: &String, file: &Path, is_stdlib: bool, project_root: &Path) {
    println!("Compiling {:?}", file);
    let gwydion_jar = project_root.join("build/libs/gwydion.jar");

    let output_dir = project_root.join("bard/output/ll");
    fs::create_dir_all(&output_dir).expect("Failed to create output directory");

    let mut command = Command::new("java");
    command.arg("-jar")
        .arg(&gwydion_jar)
        .arg(file)
        .arg(name)
        .arg("signatures.json")
        .current_dir(&output_dir);

    if is_stdlib {
        command.arg("--internal-compile-stdlib");
    }

    let output = command.output().expect("Failed to execute java process");
    output.stdout.iter().for_each(|b| print!("{}", *b as char));
    output.stderr.iter().for_each(|b| eprint!("{}", *b as char));
}

#[derive(Deserialize)]
pub struct ModuleProperties{
    name: String
}

fn parse_properties(module_root: &Path) -> ModuleProperties {
    // Now we just need to read and parse module_root/module.toml
    // print files under module_root
    for entry in fs::read_dir(module_root).expect("Failed to read directory") {
        let entry = entry.expect("Failed to read entry");
        println!("{:?}", entry.path());
    }

    let toml_path = module_root.join("module.toml");
    let toml_str = fs::read_to_string(&toml_path).expect("Failed to read module.toml");
    toml::from_str(&toml_str).expect("Failed to parse module.toml")
}