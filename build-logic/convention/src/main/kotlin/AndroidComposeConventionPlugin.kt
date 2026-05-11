import com.android.build.api.dsl.LibraryExtension
import com.mango.fakestore.buildlogic.configureCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin `mango.android.compose`. Activa el plugin de Compose y añade las
 * dependencias comunes (BOM, ui, material3, tooling, lifecycle-compose).
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            extensions.configure<LibraryExtension> {
                configureCompose(this)
            }
        }
    }
}
