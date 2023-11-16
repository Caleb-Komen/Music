plugins {
    id("music.android.feature")
}

android {
    namespace = "com.techdroidcentre.artists"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.media3.session)

    implementation(libs.androidx.navigation.compose)
}