// Módulo "convention" del composite build. Compila los plugins Kotlin DSL y los expone con un
// `id` estable (`mango.android.feature`, etc.) que los módulos consumidores aplican.

plugins {
    `kotlin-dsl`
}

group = "com.mango.fakestore.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.hilt.gradle.plugin)
    compileOnly(libs.detekt.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "mango.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "mango.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "mango.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "mango.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("androidHilt") {
            id = "mango.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidCompose") {
            id = "mango.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("detekt") {
            id = "mango.detekt"
            implementationClass = "DetektConventionPlugin"
        }
        register("kover") {
            id = "mango.kover"
            implementationClass = "KoverConventionPlugin"
        }
    }
}
