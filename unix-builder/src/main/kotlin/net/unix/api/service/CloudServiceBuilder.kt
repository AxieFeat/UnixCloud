package net.unix.api.service

import net.unix.api.builder.Builder
import net.unix.api.group.CloudGroup

interface CloudServiceBuilder : Builder<CloudService> {

    fun group(group: CloudGroup): CloudServiceBuilder

    override fun build(): CloudService {
        TODO("Not yet implemented")
    }
}