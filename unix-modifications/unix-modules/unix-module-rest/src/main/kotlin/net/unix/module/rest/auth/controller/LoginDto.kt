package net.unix.module.rest.auth.controller

data class LoginDto(
    val username: String,
    val password: String
)