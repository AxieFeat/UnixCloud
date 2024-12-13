package net.unix.api.network.universe.file

import net.unix.api.network.client.Client
import net.unix.api.network.server.Server
import net.unix.api.network.universe.Network
import net.unix.scheduler.SchedulerType
import net.unix.scheduler.impl.scheduler
import java.io.File
import java.nio.file.Files
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import kotlin.math.min

/**
 * This interface represents a builder for file transferring.
 *
 * @see SendFile
 * @see RequestFile
 */
interface FileTransfer {

    companion object {

        /**
         * Create instance of [SendFile] for sending file.
         */
        fun send(): SendFile {
            return SendFile()
        }

        /**
         * Create instance of [RequestFile] for requesting file.
         */
        fun request(): RequestFile {
            return RequestFile()
        }
    }

    /**
     * Calls on send progress update.
     */
    fun onProgressUpdate(progressTask: ProgressTask): FileTransfer

    /**
     * Calls on successful transferring.
     */
    fun onSuccess(callable: Callable<Unit>): FileTransfer

    /**
     * Where are file will be saved.
     */
    fun saveOn(path: String): FileTransfer

    fun interface ProgressTask {
        fun run(progress: ProgressUpdate)
    }

}

class SendFile : FileTransfer {

    private var uuid: String? = null

    private var file: File? = null
    private var progressTask: FileTransfer.ProgressTask? = null
    private var onSuccess: Callable<Unit>? = null

    private var saveOn: String? = null

    fun fromRequest(request: FileRequest): SendFile {
        setFile(File(request.path!!))
        saveOn(request.saveOn!!)
        this.uuid = request.uuid

        return this
    }

    fun setFile(file: File): SendFile {
        this.file = file
        return this
    }

    override fun onProgressUpdate(progressTask: FileTransfer.ProgressTask): SendFile {
        this.progressTask = progressTask
        return this
    }

    override fun onSuccess(callable: Callable<Unit>): SendFile {
        this.onSuccess = callable
        return this
    }

    override fun saveOn(path: String): SendFile {
        this.saveOn = path
        return this
    }

    fun sendBy(network: Network) {
        scheduler(SchedulerType.EXECUTOR) {
            execute {

                fun calculateSpeed(bytesSent: Int): Long {
                    return bytesSent / TimeUnit.SECONDS.toMillis(1)
                }

                val uuid: String = uuid ?: UUID.randomUUID().toString()

                val bytes = Files.readAllBytes(file!!.toPath())

                val totalSize = bytes.size

                var sentBytes = 0
                val chunkSize = 1024

                while (sentBytes < totalSize) {

                    val remaining = min(chunkSize.toDouble(), (totalSize - sentBytes).toDouble()).toInt()
                    val chunk = FileTransferChunk(uuid, saveOn, file!!.name,
                        (sentBytes + chunkSize) >= totalSize, ByteArray(remaining))
                    System.arraycopy(bytes, sentBytes, chunk.bytes, 0, remaining)

                    sentBytes += remaining

                    val progress = (sentBytes.toFloat() / totalSize * 100).toInt()
                    val update = ProgressUpdate(uuid, progress, calculateSpeed(sentBytes))

                    progressTask?.run(update)

                    network.sendObject(chunk)
                    network.sendObject(update)
                }

                onSuccess?.call()
            }
        }
    }

    fun sendBy(send: Pair<Network, Int>) {
        scheduler(SchedulerType.EXECUTOR) {
            execute {

                fun calculateSpeed(bytesSent: Int): Long {
                    return bytesSent / TimeUnit.SECONDS.toMillis(1)
                }

                val uuid: String = uuid ?: UUID.randomUUID().toString()

                val bytes = Files.readAllBytes(file!!.toPath())

                val totalSize = bytes.size

                var sentBytes = 0
                val chunkSize = 1024

                while (sentBytes < totalSize) {

                    val remaining = min(chunkSize.toDouble(), (totalSize - sentBytes).toDouble()).toInt()
                    val chunk = FileTransferChunk(uuid, saveOn, file!!.name,
                        (sentBytes + chunkSize) >= totalSize, ByteArray(remaining))
                    System.arraycopy(bytes, sentBytes, chunk.bytes, 0, remaining)

                    sentBytes += remaining

                    val progress = (sentBytes.toFloat() / totalSize * 100).toInt()
                    val update = ProgressUpdate(uuid, progress, calculateSpeed(sentBytes))

                    progressTask?.run(update)

                    send.first.sendObject(send.second, chunk)
                    send.first.sendObject(send.second, update)
                }
                onSuccess?.call()
            }
        }
    }

}

class RequestFile : FileTransfer {

    private var requestFile: String? = null
    var progressTask: FileTransfer.ProgressTask? = null
    var onSuccess: Callable<Unit>? = null

    private var saveOn: String? = null

    fun requestFile(path: String): RequestFile {
        this.requestFile = path
        return this
    }

    override fun onProgressUpdate(progressTask: FileTransfer.ProgressTask): RequestFile {
        this.progressTask = progressTask
        return this
    }

    override fun onSuccess(callable: Callable<Unit>): RequestFile {
        this.onSuccess = callable
        return this
    }

    override fun saveOn(path: String): RequestFile {
        this.saveOn = path
        return this
    }

    fun sendBy(network: Network) {
        val uuid = UUID.randomUUID().toString()
        val request = FileRequest(uuid, requestFile!!, saveOn!!)

        if(network is Client) network.fileListener.requestedFiles[uuid] = this
        if(network is Server) network.fileListener.requestedFiles[uuid] = this

        network.sendObject(request)
    }

    fun sendBy(send: Pair<Network, Int>) {
        val network = send.first

        val uuid = UUID.randomUUID().toString()
        val request = FileRequest(uuid, requestFile!!, saveOn!!)

        if(network is Client) network.fileListener.requestedFiles[uuid] = this
        if(network is Server) network.fileListener.requestedFiles[uuid] = this

        network.sendObject(send.second, request)
    }

}