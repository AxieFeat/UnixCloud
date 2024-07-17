package net.unix.api.module

import java.io.File

/**
 * Create an inheritor of this class to create a module.
 *
 * Also don't forget that to create a module, you must create a module.json file
 * or use the @ModuleInfo annotation for inheritor class to create a module
 */
abstract class CloudModule {

    /**
     * Module folder
     */
    lateinit var dataFolder: File

    /**
     * Module executable file location
     */
    lateinit var moduleFile: File

    /**
     * Module info
     *
     * name, version, authors and etc
     */
    lateinit var moduleData: ModuleData

    /**
     * Call when module loaded
     */
    open fun onLoad() {}

    /**
     * Call when module reload
     */
    open fun onReload() {}

    /**
     * Call when module unloaded
     */
    open fun onUnload() {}
}