import com.android.build.api.dsl.ApplicationExtension
import com.mango.fakestore.buildlogic.configureAndroidCommon
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin `mango.android.application`. Aplica a `:app` y a cualquier otro módulo
 * que produzca un APK. Centraliza la configuración Android común.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("mango.detekt")
                apply("mango.kover")
            }
            extensions.configure<ApplicationExtension> {
                configureAndroidCommon(this)
                defaultConfig.targetSdk = 36
            }
        }
    }
}
