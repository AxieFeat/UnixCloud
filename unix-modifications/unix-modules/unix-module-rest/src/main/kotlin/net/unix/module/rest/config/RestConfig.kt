package net.unix.module.rest.config

import org.apache.commons.lang3.RandomStringUtils

class RestConfig(
    val port: Int = 8585,
    val secret: String = RandomStringUtils.randomAlphabetic(170)
)