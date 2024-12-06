package net.unix.module.rest.controller

import net.unix.module.rest.exception.ElementAlreadyExistException

interface ExceptionHelper {

    fun throwElementAlreadyExist(): Nothing = throw ElementAlreadyExistException()

    fun throwNoSuchElement(): Nothing = throw NoSuchElementException("Element not found")

}