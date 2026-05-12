package com.mango.fakestore.features.profile.data.mapper

import com.mango.fakestore.features.profile.data.local.entity.PerfilEntity
import com.mango.fakestore.features.profile.data.remote.dto.UsuarioDto
import com.mango.fakestore.features.profile.domain.model.Usuario

fun UsuarioDto.toDomain(): Usuario = Usuario(
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

fun Usuario.toEntity(): PerfilEntity = PerfilEntity(
    id = id,
    nombreCompleto = nombreCompleto,
    nombreUsuario = nombreUsuario,
    email = email,
    telefono = telefono,
    ciudad = ciudad,
    calle = calle,
    numeroCalle = numeroCalle,
    codigoPostal = codigoPostal,
    cachadoEn = System.currentTimeMillis(),
)

fun PerfilEntity.toDomain(): Usuario = Usuario(
    id = id,
    nombreCompleto = nombreCompleto,
    nombreUsuario = nombreUsuario,
    email = email,
    telefono = telefono,
    ciudad = ciudad,
    calle = calle,
    numeroCalle = numeroCalle,
    codigoPostal = codigoPostal,
)
