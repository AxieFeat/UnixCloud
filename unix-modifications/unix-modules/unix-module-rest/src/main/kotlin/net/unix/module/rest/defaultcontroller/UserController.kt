package net.unix.module.rest.defaultcontroller

import net.unix.module.rest.annotation.*
import net.unix.module.rest.auth.AuthService
import net.unix.module.rest.auth.user.User
import net.unix.module.rest.controller.Controller

@Suppress("unused")
@RestController("user/")
class UserController(
    private val authService: AuthService
) : Controller {

    @RequestMapping(RequestType.GET, "", "web.user.get.all")
    fun handleGetAllUsers(): List<User> {
        return authService.getUsers()
    }

    @RequestMapping(RequestType.GET, "self/", "web.user.get.self")
    fun handleGetSelfUser(@RequestingUser user: User): User {
        return user
    }

    @RequestMapping(RequestType.GET, "name/:name", "web.user.get.one")
    fun handleGetOneUsers(@RequestPathParam("name") username: String): User {
        return authService.handleUserGet(username) ?: throwNoSuchElement()
    }

    @RequestMapping(RequestType.POST, "", "web.user.create")
    fun handleCreateUser(@RequestBody user: User): User {
        return authService.handleAddUser(user)
    }

    @RequestMapping(RequestType.PUT, "", "web.user.update")
    fun handleUpdateUser(@RequestBody user: User): User {
        return authService.handleUserUpdate(user)
    }

    @RequestMapping(RequestType.DELETE, "", "web.user.delete")
    fun handleDeleteUser(@RequestBody user: User): User {
        authService.handleUserDelete(user)
        return user
    }

}