rootProject.name = "selene"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

include("legacy-llvm")
include("frontend")
include("compiler")
include("ir")
include("runtime")
include("analysis")
include("compiler")
include("tools")

include("backend")
include("backend:common")
include("backend:llvm")

include("ryujin")
include("runestone")