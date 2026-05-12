import com.mango.fakestore.buildlogic.libsCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin `mango.android.hilt`. Activa Hilt + KSP y declara las dependencias mínimas
 * (`hilt-android` runtime y `hilt-android-compiler` para procesado).
 */
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
                apply("com.google.dagger.hilt.android")
            }
            val libs = libsCatalog()
            dependencies {
                "implementation"(libs.findLibrary("hilt-android").get())
                "ksp"(libs.findLibrary("hilt-compiler").get())
            }
        }
    }
}
