import com.android.build.api.dsl.LibraryExtension
import com.mango.fakestore.buildlogic.configureAndroidCommon
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin `mango.android.library`. Aplica a módulos Android library
 * (`:core:design-system`, `:core:network`, `:features:*:data`, etc.).
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("mango.detekt")
                apply("mango.kover")
            }
            extensions.configure<LibraryExtension> {
                configureAndroidCommon(this)
            }
        }
    }
}
