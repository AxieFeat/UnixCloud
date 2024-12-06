package net.unix.module.rest.auth

import net.unix.module.rest.RestModule
import net.unix.module.rest.auth.controller.LoginDto
import net.unix.module.rest.auth.user.User
import net.unix.module.rest.auth.user.permission.Permission
import net.unix.module.rest.controller.ExceptionHelper
import net.unix.module.rest.jsonlib.JsonLib
import org.apache.commons.lang3.RandomStringUtils
import java.io.File

class AuthService : ExceptionHelper {

    private val usersFile = File(RestModule.instance.folder,"rest/users.json")
    private val users: MutableList<User>

    init {
        if (!usersFile.exists()) {
            this.users = ArrayList()
            val standardUser = User("admin", RandomStringUtils.randomAlphabetic(32))
            standardUser.addPermission(Permission("*", true))
            this.users.add(standardUser)
            saveUpdateToFile()
        } else {
            this.users = JsonLib.fromJsonFile(this.usersFile)!!.getObject(Array<User>::class.java).toMutableList()
        }
    }

    fun handleLogin(login: LoginDto): String? {
        val user = getUserByName(login.username)
        if (user == null || user.password != login.password)
            return null
        return JwtProvider.instance.provider.generateToken(user)
    }

    fun handleAddUser(user: User): User {
        if (doesUserExist(user.username))
            throwElementAlreadyExist()

        val user = cloneUser(user)
        this.users.add(user)
        saveUpdateToFile()
        return user
    }

    fun handleUserUpdate(user: User): User {
        if (!doesUserExist(user.username))
            throwNoSuchElement()

        val user = cloneUser(user)
        this.users.removeIf { it.username == user.username }
        this.users.add(user)
        saveUpdateToFile()
        return user
    }

    fun handleUserDelete(user: User) {
        if (!doesUserExist(user.username))
            throwNoSuchElement()

        this.users.removeIf { it.username == user.username }
        saveUpdateToFile()
    }

    fun handleUserGet(username: String): User? {
        return getUserByName(username)
    }

    fun getUserByName(userName: String): User? {
        return this.users.firstOrNull { it.username == userName }
    }

    fun doesUserExist(userName: String): Boolean {
        return getUserByName(userName) != null
    }

    fun getUsers(): List<User> {
        return this.users
    }

    private fun cloneUser(user: User): User {
        return User(user.username, user.password)
    }

    private fun saveUpdateToFile() {
        JsonLib.fromObject(users).saveAsFile(usersFile)
    }

    class UserAlreadyExistException() : Exception("The user does already exist")

    class UserNotExistException() : Exception("The user does not exist")

}