package net.unix.module.rest.auth.user.permission.entity

import net.unix.module.rest.auth.user.permission.Permission

interface PermissionEntity {
    /**
     * Returns whether the group has the specified [permission]
     */
    fun hasPermission(permission: String): Boolean {
        if (permission.isBlank()) return true
        val permissionObj = getPermissionByMatch(permission) ?: return hasAllRights()
        return permissionObj.active
    }

    fun getPermissionByMatch(permission: String): Permission? {
        val notExpiredPermissions = getPermissions()
        return notExpiredPermissions.firstOrNull { it.matches(permission) }
    }

    /**
     * Returns the permission object of the specified [permission]
     */
    fun getPermissionByName(permission: String): Permission? =
        getPermissions().firstOrNull { it.permissionString == permission }

    /**
     * Returns all permissions of this entity
     */
    fun getPermissions(): Collection<Permission>

    /**
     * Adds the specified [permission] to the list
     */
    fun addPermission(permission: Permission)

    /**
     * Removes the permission object found by the specified [permissionString]
     */
    fun removePermission(permissionString: String)

    /**
     * Removes all permissions from this entity
     */
    fun clearAllPermission()

    /**
     * Returns whether this
     */
    fun hasAllRights(): Boolean = getPermissions().any { it.permissionString == "*" }
}