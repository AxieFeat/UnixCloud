package net.unix.api.service

import net.unix.api.group.CloudGroup
import java.io.File

interface CloudService {
    val group: CloudGroup
    val dataFolder: File
    val core: File

    fun sendCommand(line: String): String

    companion object
}