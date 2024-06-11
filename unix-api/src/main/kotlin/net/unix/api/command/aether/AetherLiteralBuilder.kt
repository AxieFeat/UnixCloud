package net.unix.api.command.aether

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.unix.api.command.sender.CommandSender

/**
 * Класс для создания аргумента команды
 *
 * @param literal Название литерала
 */
class AetherLiteralBuilder(
    literal: String
) : LiteralArgumentBuilder<CommandSender>(literal) {

    companion object {
        /**
         * Функция для создания аргумента команды
         *
         * @param literal Название литерала
         */
        fun literal(literal: String): AetherLiteralBuilder {
            return AetherLiteralBuilder(literal)
        }
    }
}