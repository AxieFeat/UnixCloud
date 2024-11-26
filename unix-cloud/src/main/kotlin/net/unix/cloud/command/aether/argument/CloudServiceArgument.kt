package net.unix.cloud.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.command.CommandArgument
import net.unix.cloud.command.aether.SyntaxExceptionBuilder
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Command argument for [CloudService].
 */
@Suppress("unused")
class CloudServiceArgument : CommandArgument<CloudService>() {

    private var notFoundMessage = "CloudService not found"

    companion object : KoinComponent {

        private val cloudServiceManager: CloudServiceManager by inject()

        /**
         * Get [CloudService] from command context by argument name.
         *
         * @param name Argument name.
         *
         * @return Instance of [CloudService].
         *
         * @throws IllegalArgumentException If argument not found or is not [CloudService].
         */
        @Throws(IllegalArgumentException::class)
        fun CommandContext<*>.getCloudService(name: String): CloudService {
            return this.getArgument(name, CloudService::class.java)
        }
    }

    override fun parse(reader: StringReader): CloudService {
        val rawName = reader.readString()

        val uuid = if(rawName.contains(" ")) {
            UUID.fromString(
                rawName.split(" ")[1]
                    .replace("(", "")
                    .replace(")", "")
            )
        } else {
            cloudServiceManager[rawName].first().uuid
        }

        val service = cloudServiceManager[uuid]
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
        return cloudServiceManager.services.map { it.name }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        cloudServiceManager.services.forEach {
            if (it.name.contains(" ")) {
                builder.suggest("\"${it.name}\"")
            } else
                builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}