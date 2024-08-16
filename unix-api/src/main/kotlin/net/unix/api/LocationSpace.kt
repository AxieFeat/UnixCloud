package net.unix.api

import java.io.File
import net.unix.api.group.CloudGroup
import net.unix.api.service.CloudService
import net.unix.api.template.CloudTemplate
import net.unix.api.modification.module.CloudModule

interface LocationSpace {

    /**
     * Directory for [CloudModule]'s
     */
    val module: File

    /**
     * Directory for [CloudGroup]'s
     */
    val group: File

    /**
     * Directory for [CloudService]'s
     */
    val service: File

    /**
     * Directory for [CloudTemplate]'s
     */
    val template: File
}