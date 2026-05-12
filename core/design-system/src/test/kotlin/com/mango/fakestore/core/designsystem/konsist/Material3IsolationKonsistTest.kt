package com.mango.fakestore.core.designsystem.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withImportNamed
import org.junit.Test

class Material3IsolationKonsistTest {

    private val material3Package = "androidx.compose.material3"

    private val allowedPackages = setOf(
        "com.mango.fakestore.core.designsystem",
        "com.mango.fakestore.core.ui",
        "com.example.fakestoreapp",
    )

    private val allowedClassesInCoreUi = setOf("Surface", "Scaffold", "Snackbar")

    @Test
    fun `solo core_designsystem y excepciones en core_ui importan material3`() {
        Konsist.scopeFromProject()
            .files
            .filter { file ->
                file.imports.any { it.name.startsWith(material3Package) }
            }
            .forEach { file ->
                val packageName = file.packagee?.name ?: ""
                val isAllowedPackage = allowedPackages.any { packageName.startsWith(it) }
                assert(isAllowedPackage) {
                    "Importación de Material3 no permitida en: ${file.path} (paquete: $packageName)"
                }
            }
    }
}
