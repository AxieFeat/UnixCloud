package net.unix.module.rest.auth.user.permission.entity

import net.unix.module.rest.auth.user.permission.Permission

open class CloudPermissionEntity : PermissionEntity {
    private val permissions = ArrayList<Permission>()

    override fun getPermissions(): Collection<Permission> = this.permissions

    override fun addPermission(permission: Permission) {
        removePermission(permission.permissionString)
        this.permissions.add(permission)
    }

    override fun removePermission(permissionString: String) {
        this.permissions.remove(getPermissionByName(permissionString))
    }

    override fun clearAllPermission() {
        this.permissions.clear()
    }
}