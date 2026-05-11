// Módulo `:features:favorites:data` — Submódulo data de feature.
// Aplica mango.android.library y mango.android.hilt para inyectar dependencias.
plugins {
    id("mango.android.library")
    id("mango.android.hilt")
}

android {
    namespace = "com.mango.fakestore.features.favorites.data"
}
