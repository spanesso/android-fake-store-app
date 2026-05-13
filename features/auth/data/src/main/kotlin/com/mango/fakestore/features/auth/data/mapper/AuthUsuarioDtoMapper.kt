package com.mango.fakestore.features.auth.data.mapper

import com.mango.fakestore.features.auth.data.remote.dto.AuthUsuarioDto
import com.mango.fakestore.features.profile.data.local.entity.PerfilEntity
import com.mango.fakestore.features.profile.domain.model.Usuario

fun AuthUsuarioDto.toDomain(): Usuario = Usuario(
    id = id,
    nombreCompleto = "${name.firstname} ${name.lastname}",
    nombreUsuario = username,
    email = email,
    telefono = phone,
    ciudad = address.city,
    calle = address.street,
    numeroCalle = address.number,
    codigoPostal = address.zipcode,
)

fun AuthUsuarioDto.toEntity(): PerfilEntity = PerfilEntity(
    id = id,
    nombreCompleto = "${name.firstname} ${name.lastname}",
    nombreUsuario = username,
    email = email,
    telefono = phone,
    ciudad = address.city,
    calle = address.street,
    numeroCalle = address.number,
    codigoPostal = address.zipcode,
    cachadoEn = System.currentTimeMillis(),
)
