// Módulo `:features:auth:api` — Kotlin puro. Convention plugin: mango.android.library.
plugins {
    id("mango.android.library")
}

android {
    namespace = "com.mango.fakestore.features.auth.api"
}

dependencies {
    api(project(":features:auth:domain"))
}
