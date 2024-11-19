package net.unix.api.configuration

interface Configuration {

    val location: String

    fun save(): Map<String, Any>

}