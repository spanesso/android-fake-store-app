// Composite build "build-logic". Aloja los convention plugins Gradle que aplican configuración
// reutilizable a los módulos `:app`, `:core:*` y `:features:*`. Ningún módulo del proyecto debe
// declarar configuración Android cruda en su `build.gradle.kts`; aplica el convention plugin
// correspondiente (mango.android.*, mango.kotlin.library, etc.).

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
