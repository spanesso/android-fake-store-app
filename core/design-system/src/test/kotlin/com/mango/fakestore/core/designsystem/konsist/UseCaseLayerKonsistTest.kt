package com.mango.fakestore.core.designsystem.konsist

import com.lemonappdev.konsist.api.Konsist
import org.junit.Test

class UseCaseLayerKonsistTest {

    @Test
    fun `ningun usecase importa clases de la capa data`() {
        Konsist.scopeFromProject()
            .files
            .filter { file ->
                file.packagee?.name?.contains(".domain.casosdeuso") == true
            }
            .forEach { file ->
                val importaciosDeData = file.imports.filter { importDeclaration ->
                    importDeclaration.name.contains(".data.")
                }
                assert(importaciosDeData.isEmpty()) {
                    "UseCase '${file.name}' importa de la capa data — " +
                        "viola inversión de dependencias: " +
                        importaciosDeData.map { it.name }
                }
            }
    }

    @Test
    fun `todos los usecases tienen anotacion Inject en constructor`() {
        Konsist.scopeFromProject()
            .files
            .filter { file ->
                file.packagee?.name?.contains(".domain.casosdeuso") == true
            }
            .forEach { file ->
                val tieneInject = file.text.contains("@Inject")
                assert(tieneInject) {
                    "UseCase '${file.name}' no tiene @Inject constructor — Hilt no puede inyectarlo automáticamente"
                }
            }
    }
}
