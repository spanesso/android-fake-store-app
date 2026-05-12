import com.mango.fakestore.buildlogic.libsCatalog
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin `mango.detekt`. Aplica Detekt con configuración compartida y, si existe,
 * un `detekt-baseline.xml` para no romper la primera CI. La config vive en
 * `config/detekt/detekt.yml` en la raíz del proyecto y se replica solo si el módulo no la
 * sobreescribe.
 */
class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")
            val libs = libsCatalog()
            extensions.configure<DetektExtension> {
                buildUponDefaultConfig = true
                allRules = false
                config.setFrom(rootProject.files("config/detekt/detekt.yml"))
                baseline = rootProject.file("config/detekt/detekt-baseline-${name}.xml")
                parallel = true
                ignoreFailures = false
            }
            dependencies {
                "detektPlugins"(libs.findLibrary("detekt-formatting").get())
            }
        }
    }
}
