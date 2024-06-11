package net.unix.api.service

interface ServiceManager {
    fun getService(name: String): CloudService?
}