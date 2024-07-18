package net.unix.api.command.aether

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.tree.ArgumentCommandNode
import net.unix.api.command.sender.CommandSender

/**
 * Command required argument builder
 *
 * @param T Object type
 * @param name Argument name
 * @param type [ArgumentType], which returns [T]
 * @param suggestionsProvider Argument suggestions
 */
class AetherArgumentBuilder<T>(
    val name: String,
    private val type: ArgumentType<T>,
    private var suggestionsProvider: SuggestionProvider<CommandSender>? = null
) : ArgumentBuilder<CommandSender, AetherArgumentBuilder<T>>() {

    override fun getThis(): AetherArgumentBuilder<T> {
        return this
    }

    /**
     * Create argument suggestions
     *
     * @param provider Command suggestions
     *
     * @return Current [AetherArgumentBuilder] instance
     */
    fun suggests(provider: SuggestionProvider<CommandSender>?): AetherArgumentBuilder<T> {
        this.suggestionsProvider = provider
        return getThis()
    }

    /**
     * Build argument for future usage
     *
     * @return [ArgumentCommandNode] instance
     */
    override fun build(): ArgumentCommandNode<CommandSender, T> {
        val result: ArgumentCommandNode<CommandSender, T> = ArgumentCommandNode(
            name, type,
            command, requirement, redirect, redirectModifier, isFork, suggestionsProvider
        )

        for (argument in arguments) {
            result.addChild(argument)
        }

        return result
    }

    /**
     * Command executor
     *
     * @param aetherCommand The result of the command execution. Always return 1
     *
     * @return Current instance of [AetherArgumentBuilder]
     */
    fun execute(aetherCommand: AetherCommand<CommandSender>?): AetherArgumentBuilder<T> {
        val command = Command { context ->
            aetherCommand?.run(context)
            1
        }
        return super.executes(command) as AetherArgumentBuilder<T>
    }

    companion object {
        /**
         * Create instance of [AetherArgumentBuilder]
         *
         * @param T Object type
         * @param name Argument name
         * @param type [ArgumentType], which returns [T]
         */
        fun <T> argument(name: String, type: ArgumentType<T>): AetherArgumentBuilder<T> {
            return AetherArgumentBuilder(name, type)
        }
    }

}