package net.unix.api.service

interface CloudServiceManager {
    fun getService(name: String): CloudService?
}