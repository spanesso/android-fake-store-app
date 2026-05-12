plugins {
    id("mango.android.library")
}

android {
    namespace = "com.mango.fakestore.core.error"
}

dependencies {
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)
    compileOnly(libs.androidx.annotation)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
}
