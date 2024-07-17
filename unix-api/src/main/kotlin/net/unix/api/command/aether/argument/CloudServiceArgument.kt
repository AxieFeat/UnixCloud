package net.unix.api.command.aether.argument

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.CloudAPI
import net.unix.api.command.aether.AetherArgument
import net.unix.api.service.CloudService
import java.util.concurrent.CompletableFuture

class CloudServiceArgument : AetherArgument<CloudService>() {

    private var notFoundMessage = "CloudService not found!"

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

    override fun parse(reader: StringReader): CloudService {
        val service = CloudAPI.instance.cloudServiceManager.getService(reader.readString())
            ?: throw CommandSyntaxException(dynamic2CommandExceptionType, LiteralMessage(notFoundMessage))

        return service
    }

    fun notFound(message: String): CloudServiceArgument {
        this.notFoundMessage = message

        return this
    }

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
      //  CloudAPI.instance.cloudServiceManager.services.forEach {
         //   builder.suggest(it.name)
      //  }

        return builder.buildFuture()
    }
}