plugins {
    id("mango.android.library")
    id("mango.android.hilt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.mango.fakestore.core.network"

    buildFeatures {
        buildConfig = true
    }

    flavorDimensions += "env"
    productFlavors {
        create("dev") {
            dimension = "env"
            buildConfigField("String", "BASE_URL", "\"https://fakestoreapi.com/\"")
        }
        create("staging") {
            dimension = "env"
            buildConfigField("String", "BASE_URL", "\"https://fakestoreapi.com/\"")
        }
        create("prod") {
            dimension = "env"
            buildConfigField("String", "BASE_URL", "\"https://fakestoreapi.com/\"")
        }
    }
}

dependencies {
    implementation(project(":core:error"))
    implementation(project(":core:common"))

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.arrow.core)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.robolectric)
    testImplementation(libs.turbine)
}
