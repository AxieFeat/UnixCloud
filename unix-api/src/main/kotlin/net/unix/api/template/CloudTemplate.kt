package net.unix.api.template

import net.unix.api.group.CloudFile
import java.io.File

interface CloudTemplate {
    var templateFolder: File
    val files: MutableList<CloudFile>

    companion object
}