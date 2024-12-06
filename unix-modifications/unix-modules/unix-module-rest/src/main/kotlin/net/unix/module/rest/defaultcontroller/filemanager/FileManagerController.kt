package net.unix.module.rest.defaultcontroller.filemanager

import io.javalin.core.util.FileUtil
import io.javalin.http.Context
import net.unix.module.rest.annotation.RequestMapping
import net.unix.module.rest.annotation.RequestType
import net.unix.module.rest.annotation.RestController
import net.unix.module.rest.controller.Controller
import net.unix.node.logging.CloudLogger
import java.io.File

@Suppress("unused")
@RestController("filemanager/")
class FileManagerController : Controller {

    @RequestMapping(RequestType.GET, "*", "web.filemanager.read")
    fun handleRead(ctx: Context): Collection<FileInfo> {
        checkForSuspiciousPath(ctx)
        val file = getFileFromRequest(ctx)
        if (!file.exists()) throwNoSuchElement()
        if (file.isDirectory) {
            val listFiles = file.listFiles()!!.toList()
            val directories = listFiles.filter { it.isDirectory }.sortedBy { it.name }
            val files = listFiles.filter { !it.isDirectory }.sortedBy { it.name }
            return directories.union(files).map { FileInfo(it.name, it.isDirectory, it.length()) }
        }
        if (ctx.queryParam("view") == null)
            ctx.res.setHeader("Content-Disposition", "attachment; filename=${file.name}")
        ctx.result(file.readBytes())
        return emptyList()
    }

    @RequestMapping(RequestType.DELETE, "*", "web.filemanager.delete")
    fun handleDelete(ctx: Context): Boolean {
        checkForSuspiciousPath(ctx)
        val file = getFileFromRequest(ctx)
        if (!file.exists()) throwNoSuchElement()
        if (file.isDirectory) {
            //FileUtils.deleteDirectory(file)
        } else {
            file.delete()
        }
        return true
    }

    @RequestMapping(RequestType.POST, "*", "web.filemanager.create")
    fun handleCreate(ctx: Context): Boolean {
        checkForSuspiciousPath(ctx)
        val file = getFileFromRequest(ctx)
        println(file.absolutePath)
        if (file.exists() && file.isDirectory) return false
        if (file.exists()) file.delete()
        file.parentFile?.mkdirs()
        val uploadedFile = ctx.uploadedFile("file")

        if (uploadedFile != null) {
            FileUtil.streamToFile(uploadedFile.content, file.absolutePath)
        } else {
            CloudLogger.info(ctx.body())
            if (ctx.body().isNotEmpty()) {
                file.writeText(ctx.body())
            }
        }
        return true
    }

    private fun getFileFromRequest(ctx: Context): File {
        val filepath = ctx.req.pathInfo.replace("/filemanager/", "")
        if (filepath.isBlank()) return File(".")
        return File(filepath)
    }

    private fun checkForSuspiciousPath(ctx: Context) {
        if (ctx.req.pathInfo.contains("..") || ctx.req.pathInfo.contains("//"))
            throw InvalidPathException("Invalid file path")
    }

    class FileAlreadyExistsException(message: String) : Exception(message)

    class InvalidPathException(message: String) : Exception(message)

    class FileInfo(val name: String, val isDirectory: Boolean, val size: Long)

}