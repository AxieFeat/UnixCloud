package net.unix.module.rest

import net.unix.module.rest.auth.JwtProvider
import net.unix.module.rest.config.RestConfig
import net.unix.module.rest.config.RestConfigLoader
import net.unix.module.rest.javalin.RestServer
import net.unix.module.rest.util.executeWithDifferentContextClassLoader
import net.unix.node.logging.CloudLogger
import net.unix.node.modification.module.CloudModule

class RestModule : CloudModule() {

    companion object {
        lateinit var instance: RestModule
    }

    override fun onLoad() {
        instance = this

        CloudLogger.info("Initializing REST module...")
        
        val config = loadConfig()
        val moduleClassLoader = this::class.java.classLoader
        executeWithDifferentContextClassLoader(moduleClassLoader) {
            startRestServer(config)
        }
        CloudLogger.info("REST module loaded")
    }

    private fun loadConfig(): RestConfig {
        return RestConfigLoader().loadConfig()
    }

    private fun startRestServer(config: RestConfig) {
        JwtProvider(config.secret)
        RestServer.start(config.port)
    }
}