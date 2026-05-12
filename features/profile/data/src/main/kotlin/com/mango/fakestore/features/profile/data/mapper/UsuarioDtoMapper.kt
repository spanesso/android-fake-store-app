package com.mango.fakestore.features.profile.data.mapper

import com.mango.fakestore.features.profile.data.remote.dto.UsuarioDto
import com.mango.fakestore.features.profile.domain.model.Usuario

fun UsuarioDto.toDomain(): Usuario = Usuario(
    id = id,
    nombreCompleto = "${name.firstname} ${name.lastname}",
    nombreUsuario = username,
    email = email,
    telefono = phone,
    ciudad = address.city,
    calle = "${address.street} ${address.number}",
    codigoPostal = address.zipcode,
)
