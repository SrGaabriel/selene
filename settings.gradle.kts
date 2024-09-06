rootProject.name = "selene"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

include("llvm")
include("frontend")
include("compiler")
include("ir")
include("runtime")
include("analysis")
include("compiler")
include("tools")

include("ryujin")
include("runestone")