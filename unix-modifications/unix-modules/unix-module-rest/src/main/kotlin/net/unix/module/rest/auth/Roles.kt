package net.unix.module.rest.auth

import io.javalin.security.RouteRole

enum class Roles : RouteRole {

    ANYONE, USER

}

fun createRolesMapping(): HashMap<String, RouteRole> {
    val rolesMapping = HashMap<String, RouteRole>()
    Roles.entries.forEach {
        rolesMapping[it.toString()] = it
    }
    return rolesMapping
}