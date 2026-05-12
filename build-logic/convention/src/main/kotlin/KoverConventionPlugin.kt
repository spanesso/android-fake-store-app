import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Convention plugin `mango.kover`. Aplica Kover a cada módulo y deja que el root agregue el
 * reporte combinado (`koverHtmlReport`, `koverXmlReport`). Los umbrales por capa
 * (domain ≥ 100%, data ≥ 80%, presentation ≥ 70%) se imponen en el root build.
 */
class KoverConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply("org.jetbrains.kotlinx.kover")
    }
}
