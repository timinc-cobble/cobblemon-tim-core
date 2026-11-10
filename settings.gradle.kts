rootProject.name = "cobblemon-tim-core"

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        gradlePluginPortal()
    }
}

include("common")

// Based on env vars, load the respective loaders
// CI var is always present on GitHub CI, LOADER trickles in publish-maven matrix
if ((System.getenv("CI") != null && System.getenv("LOADER") == "fabric") || System.getenv("CI") == null) {
    include("fabric")
}
if ((System.getenv("CI") != null && System.getenv("LOADER") == "neoforge") || System.getenv("CI") == null) {
    include("neoforge")
}
