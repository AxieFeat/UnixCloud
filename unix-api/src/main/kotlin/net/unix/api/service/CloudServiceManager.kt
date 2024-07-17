package net.unix.api.service

interface CloudServiceManager {
    val services: List<CloudService>
    fun getService(name: String): CloudService?
}