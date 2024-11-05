package net.unix.module.test

import net.unix.cloud.CloudInstance
import net.unix.cloud.modification.module.CloudModule

class Test : CloudModule() {

    override fun onLoad() {
        CloudInstance.instance.loggerFactory["test"].info("Я живой!")
    }

}