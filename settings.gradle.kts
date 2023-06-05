pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Music"
include(":app")
include(":core:data")
include(":core:model")
include(":player")
include(":feature:songs")
include(":core:common")
include(":feature:nowplaying")
