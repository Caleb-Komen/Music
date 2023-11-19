plugins {
    id("music.android.feature")
    kotlin("kapt")
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.techdroidcentre.home"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))

    implementation(libs.androidx.media3.session)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.coil.compose)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
}

kapt {
    correctErrorTypes = true
}