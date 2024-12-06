package net.unix.module.rest.auth

import io.javalin.core.security.Role

enum class Roles : Role {

    ANYONE, USER

}

fun createRolesMapping(): HashMap<String, Role> {
    val rolesMapping = HashMap<String, Role>()
    Roles.entries.forEach {
        rolesMapping[it.toString()] = it
    }
    return rolesMapping
}