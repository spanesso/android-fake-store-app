

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kover)
}

// Configuración global de Detekt: cada subproyecto aplica `mango.detekt` (build-logic) que
// ya enlaza la config compartida en config/detekt/detekt.yml.
subprojects {
    plugins.withId("io.gitlab.arturbosch.detekt") {
        extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
            config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        }
    }
}

// Tarea conveniencia para ejecutar Detekt en todos los módulos.
tasks.register("detektAll") {
    group = "verification"
    description = "Ejecuta Detekt en todos los subproyectos."
    dependsOn(subprojects.map { "${it.path}:detekt" })
}

// Configuración global de ktlint.
configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    android.set(true)
    ignoreFailures.set(false)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    filter {
        exclude { it.file.path.contains("build/") }
    }
}

// Cobertura agregada con Kover: cada módulo aplica `mango.kover` y el root agrega los
// reportes combinados (`koverHtmlReport`, `koverXmlReport`).
kover {
    reports {
        verify {
            // Los umbrales por capa (domain 100%, data ≥80%, presentation ≥70%) se imponen
            // por módulo cuando exista código. Mientras el repo esté vacío (ETAPA 0) la
            // verificación queda en modo informativo.
        }
    }
}

dependencies {
    subprojects.forEach { project ->
        project.plugins.withId("org.jetbrains.kotlinx.kover") {
            kover(project)
        }
    }
}
