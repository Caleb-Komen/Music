plugins {
    id("music.android.feature")
}

android {
    namespace = "com.techdroidcentre.albums"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.media3.session)

    implementation(libs.androidx.navigation.compose)
}