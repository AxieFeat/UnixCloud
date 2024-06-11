package net.unix.api.command.aether.argument

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.CloudAPI
import net.unix.api.service.CloudService
import java.util.concurrent.CompletableFuture

class CloudServiceArgument : ArgumentType<CloudService> {

    private val dynamic2CommandExceptionType = Dynamic2CommandExceptionType { _: Any?, _: Any? ->
        LiteralMessage(
            ""
        )
    }

    companion object {
        fun getCloudService(context: CommandContext<*>, name: String?): CloudService {
            return context.getArgument(name, CloudService::class.java)
        }
    }

    override fun parse(reader: StringReader?): CloudService {
        val service = CloudAPI.instance.serviceManager.getService(reader!!.readString())
            ?: throw CommandSyntaxException(dynamic2CommandExceptionType, LiteralMessage("CloudService not found!"))

        return service
    }

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>?,
        builder: SuggestionsBuilder?
    ): CompletableFuture<Suggestions> {
        builder?.suggest("Lobby-1")
        builder?.suggest("Lobby-2")
        builder?.suggest("Proxy-1")

        return builder!!.buildFuture()
    }
}