package net.unix.api.command.aether

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.unix.api.command.sender.CommandSender

/**
 * Command literal builder
 *
 * @param literal Literal name
 */
class AetherLiteralBuilder(
    literal: String
) : LiteralArgumentBuilder<CommandSender>(literal) {

    companion object {
        /**
         * Create instance of [AetherLiteralBuilder]
         *
         * @param literal Literal name
         */
        fun literal(literal: String): AetherLiteralBuilder {
            return AetherLiteralBuilder(literal)
        }
    }
}