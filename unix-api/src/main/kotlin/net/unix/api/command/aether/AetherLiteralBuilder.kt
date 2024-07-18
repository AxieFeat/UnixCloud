package net.unix.api.command.aether

import com.mojang.brigadier.Command
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

    /**
     * Command executor
     *
     * @param aetherCommand The result of the command execution. Always return 1
     *
     * @return Current instance of [AetherLiteralBuilder]
     */
    fun execute(aetherCommand: AetherCommand<CommandSender>?): AetherLiteralBuilder {
        val command = Command { context ->
            aetherCommand?.run(context)
            1
        }
        return super.executes(command) as AetherLiteralBuilder
    }
}