package net.unix.api.module

import java.io.File

abstract class CloudModule {
    lateinit var dataFolder: File
    lateinit var moduleFile: File
    lateinit var moduleData: ModuleData

    open fun onLoad() {}
    open fun onReload() {}
    open fun onUnload() {}
}