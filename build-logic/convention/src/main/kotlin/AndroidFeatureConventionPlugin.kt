import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin `mango.android.feature`. Aplica a módulos `:features:*:presentation`:
 * añade Compose + Hilt + Lifecycle ViewModel + Navigation. Para `data` usar
 * `mango.android.library` + `mango.android.hilt` directamente.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("mango.android.library")
                apply("mango.android.compose")
                apply("mango.android.hilt")
            }
            extensions.configure<LibraryExtension> {
                // Espacio para configuración adicional específica de features (p. ej. flavors).
            }
        }
    }
}
