package net.unix.node.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.node.command.aether.SyntaxExceptionBuilder
import net.unix.api.service.Service
import net.unix.api.service.ServiceManager
import net.unix.command.CommandArgument
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Command argument for [Service].
 */
@Suppress("unused")
class CloudServiceArgument : CommandArgument<Service>(), KoinComponent {

    private val serviceManager: ServiceManager by inject(named("default"))
    private var notFoundMessage = "CloudService not found"

    companion object {

        /**
         * Get [Service] from command context by argument name.
         *
         * @param name Argument name.
         *
         * @return Instance of [Service].
         *
         * @throws IllegalArgumentException If argument not found or is not [Service].
         */
        @Throws(IllegalArgumentException::class)
        fun CommandContext<*>.getCloudService(name: String): Service {
            return this.getArgument(name, Service::class.java)
        }
    }

    override fun parse(reader: StringReader): Service {
        val rawName = reader.readString()

        val uuid = if(rawName.contains(" ")) {
            UUID.fromString(
                rawName.split(" ")[1]
                    .replace("(", "")
                    .replace(")", "")
            )
        } else {
            serviceManager[rawName].first().uuid
        }

        val service = serviceManager[uuid]
            ?: throw SyntaxExceptionBuilder.exception(notFoundMessage, reader)

        return service
    }

    /**
     * Set your own not found message.
     *
     * @param message Message text.
     *
     * @return Current instance of [CloudServiceArgument].
     */
    fun notFound(message: String): CloudServiceArgument {
        this.notFoundMessage = message

        return this
    }

    override fun getExamples(): List<String> {
        return serviceManager.services.map { it.name }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        serviceManager.services.forEach {
            if (it.name.contains(" ")) {
                builder.suggest("\"${it.name}\"")
            } else
                builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}