package net.unix.api.command.aether

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.tree.ArgumentCommandNode
import net.unix.api.command.sender.CommandSender

/**
 * Класс для создания аргумента команды
 *
 * @param T Тип данных
 * @param name Название аргумента
 * @param type Тип аргумента, который возвращает [T]
 * @param suggestionsProvider Предложения для заполнения команды
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
     * Установка предложений для заполнения команды
     */
    fun suggests(provider: SuggestionProvider<CommandSender>?): AetherArgumentBuilder<T> {
        this.suggestionsProvider = provider
        return getThis()
    }

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

    companion object {
        fun <T> argument(name: String, type: ArgumentType<T>): AetherArgumentBuilder<T> {
            return AetherArgumentBuilder(name, type)
        }
    }

}