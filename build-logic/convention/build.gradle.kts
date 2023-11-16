plugins {
    `kotlin-dsl`
}

group = "com.techdroidcentre.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidFeature") {
            id = "music.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
    }
}