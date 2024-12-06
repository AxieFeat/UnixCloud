package net.unix.module.rest.config

import java.io.File

class RestConfigLoader : AbstractJsonLibConfigLoader<RestConfig>(
    RestConfig::class.java,
    File("modules/rest/config.json"),
    { RestConfig() },
    true
)