package net.unix.module.rest.auth.user

import net.unix.module.rest.annotation.WebExclude
import net.unix.module.rest.auth.user.permission.entity.CloudPermissionEntity

data class User(
    val username: String,
    @WebExclude
    var password: String
) : CloudPermissionEntity()