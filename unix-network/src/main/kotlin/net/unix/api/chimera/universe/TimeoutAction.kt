package net.unix.api.chimera.universe

fun interface TimeoutAction {
    fun run()

    companion object
}