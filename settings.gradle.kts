pluginManagement {
    includeBuild("build-logic")
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
include(":feature:albums")
include(":feature:albumdetails")
include(":feature:artists")
include(":feature:artistdetails")
include(":core:designsystem")
include(":feature:home")
include(":feature:playlists")
include(":core:database")
include(":feature:playlistsongs")
include(":feature:favourites")
include(":feature:topalbums")
include(":feature:recentlyplayed")
