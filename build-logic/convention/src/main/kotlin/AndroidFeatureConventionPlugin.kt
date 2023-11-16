import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

class AndroidFeatureConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with (target) {
            applyPlugins()
            androidConfig()
            dependenciesConfig()
        }
    }
}

fun Project.applyPlugins() {
    pluginManager.run {
        apply("com.android.library")
        apply("org.jetbrains.kotlin.android")
    }
}

fun Project.androidConfig() {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    val extension = extensions.getByType<LibraryExtension>()
    extension.apply {
        compileSdk = libs.findVersion("compileSdk").get().toString().toInt()
        defaultConfig {
            minSdk = libs.findVersion("minSdk").get().toString().toInt()
            targetSdk = libs.findVersion("targetSdk").get().toString().toInt()

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        buildTypes {
            release {
                isMinifyEnabled = false
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        kotlinOptions {
            jvmTarget = "17"
        }
        buildFeatures {
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("composeCompiler").get().toString()
        }
    }
}

private fun Project.dependenciesConfig() {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    dependencies {
        val bom = libs.findLibrary("androidx-compose-bom").get()
        add("implementation", platform(bom))
        add("implementation", libs.findLibrary("androidx-compose-ui").get())
        add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
        add("implementation", libs.findLibrary("androidx-compose-material3").get())
        add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
    }
}

private fun CommonExtension<*, *, *, *, *>.kotlinOptions(
    block: KotlinJvmOptions.() -> Unit
) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}
