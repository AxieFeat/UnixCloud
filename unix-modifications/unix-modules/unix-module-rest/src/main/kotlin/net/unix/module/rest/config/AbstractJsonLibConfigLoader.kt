package net.unix.module.rest.config

import com.google.gson.Gson
import net.unix.module.rest.jsonlib.JsonLib
import java.io.File

abstract class AbstractJsonLibConfigLoader<T : Any>(
    private val configClass: Class<T>,
    private val configFie: File,
    private val lazyDefaultObject: () -> T,
    private val saveDefaultOnFistLoad: Boolean,
    private val gsonToUse: Gson = JsonLib.GSON
) : ConfigLoader<T> {

    override fun loadConfig(): T {
        val objectFromFile = JsonLib.fromJsonFile(configFie, gsonToUse)?.getObjectOrNull(configClass)
        if (objectFromFile == null) {
            val defaultObject = lazyDefaultObject()
            if (saveDefaultOnFistLoad && !doesConfigFileExist())
                saveConfig(defaultObject)
            return defaultObject
        }
        return objectFromFile
    }

    override fun saveConfig(value: T) {
        JsonLib.fromObject(value, gsonToUse).saveAsFile(configFie)
    }

    override fun doesConfigFileExist(): Boolean = this.configFie.exists()


}