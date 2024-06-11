package net.unix.api.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.unix.api.command.aether.AetherArgumentBuilder
import net.unix.api.command.aether.AetherCommandBuilder
import net.unix.api.command.aether.AetherLiteralBuilder
import net.unix.api.command.sender.CommandSender

/**
 * Класс для выполнения команд
 */
interface CommandDispatcher {

    /**
     * Выполнить команду
     *
     * @param sender Отправитель команды
     * @param command Команда в виде строки
     *
     * @return 1, если команда успешно выполнена, иначе 0
     *
     * @throws CommandSyntaxException Если при выполнении команды была замечена синтаксическая ошибка
     */
    @Throws(CommandSyntaxException::class)
    fun dispatchCommand(sender: CommandSender, command: String): Int

    /**
     * Выполнить команду
     *
     * @param results Результат парсинга команда, например от метода [parseCommand]
     *
     * @return 1, если команда успешно выполнена, иначе 0
     *
     * @throws CommandSyntaxException Если при выполнении команды была замечена синтаксическая ошибка
     */
    @Throws(CommandSyntaxException::class)
    fun dispatchCommand(results: ParseResults<CommandSender>): Int

    /**
     * Парсит команду
     *
     * @param sender Отправитель команды
     * @param command Команда в виде строки
     *
     * @return Результат парсинга команды
     */
    fun parseCommand(sender: CommandSender, command: String): ParseResults<CommandSender>

    /**
     * Объект [CommandDispatcher] от библиотеки Brigadier
     *
     * Если вы хотите создать команду, то используйте классы:
     *
     * [AetherCommandBuilder],
     * [AetherArgumentBuilder],
     * [AetherLiteralBuilder]
     *
     */
    val dispatcher: CommandDispatcher<CommandSender>

}