package net.unix.api.terminal

import net.unix.api.terminal.Color.ColorExecutable
import org.fusesource.jansi.Ansi

/**
 * Coloring text like minecraft legacy colors
 *
 * @param red Red value
 * @param green Green value
 * @param blue Blue value
 * @param code Color code
 * @param function Translate color function
 */
@Deprecated("Use it only if you know, what a doing, else use Adventure Components")
enum class Color(
    red: Int,
    green: Int,
    blue: Int,
    val code: String,
    function: ColorExecutable =
        ColorExecutable { ansi ->
            return@ColorExecutable ansi.a(Ansi.Attribute.RESET).fgRgb(red, green, blue).toString()
        }
) {

    BLACK(0, 0, 0, "&0"),
    DARK_BLUE(0, 0, 170, "&1"),
    DARK_GREEN(0, 170, 0, "&2"),
    DARK_AQUA(0, 170, 170, "&3"),
    DARK_RED(170, 0, 0, "&4"),
    DARK_PURPLE(170, 0, 170, "&5"),
    GOLD(255, 170, 0, "&6"),
    GRAY(170, 170, 170, "&7"),
    DARK_GRAY(85, 85, 85, "&8"),
    BLUE(85, 85, 255, "&9"),
    RED(255, 85, 85, "&c"),
    YELLOW(255, 255, 85, "&e"),
    GREEN(85, 255, 85, "&a"),
    AQUA(85, 255, 255, "&b"),
    LIGHT_PURPLE(255, 85, 255, "&d"),
    WHITE(255, 255, 255, "&f"),

    BOLD(0, 0, 0, "&l", { ansi ->
        ansi.a(org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD).toString()
    }),
    STRIKETHROUGH(0, 0, 0, "&m", { ansi ->
        ansi.a(org.fusesource.jansi.Ansi.Attribute.STRIKETHROUGH_ON).toString()
    }),
    UNDERLINE(0, 0, 0, "&n", { ansi ->
        ansi.a(org.fusesource.jansi.Ansi.Attribute.UNDERLINE).toString()
    }),
    ITALIC(0, 0, 0, "&o", { ansi ->
        ansi.a(org.fusesource.jansi.Ansi.Attribute.ITALIC).toString()
    }),
    RESET(0, 0, 0, "&r", { ansi ->
        ansi.a(org.fusesource.jansi.Ansi.Attribute.RESET).toString()
    });

    private val jansiAnsi: Ansi = Ansi.ansi()
    private val ansi: String = function.run(jansiAnsi)

    companion object {

        private val VALUES = entries.toTypedArray()
        private const val HEX_PATTERN = "(&#[0-9a-fA-F]{6})"

        /**
         * Format text in string
         *
         * @param input Input string
         *
         * @return Changed string or null, if [input] == null
         */
        fun translate(input: String?): String? {
            var formattedText: String = input ?: return null

            for (color in VALUES) {
                val code = color.code
                formattedText = formattedText.replace(code, color.ansi)
            }

            return formattedText.replace(Regex(HEX_PATTERN)) { matchResult ->
                val hexColor = matchResult.groupValues[1]
                val color = java.awt.Color.decode(hexColor.replace("&", ""))

                val ansiColor = Ansi.ansi().a(Ansi.Attribute.RESET).fgRgb(color.red, color.green, color.blue).toString()

                return@replace ansiColor
            }
        }

        /**
         * Strip colors in string
         *
         * @param input Input string
         *
         * @return Changed string or null, if [input] == null
         */
        fun strip(input: String?): String? {
            var formattedText: String = input ?: return null

            for (color in VALUES) {
                val code = color.code
                formattedText = formattedText.replace(code, "")
            }

            return formattedText.replace(Regex(HEX_PATTERN)) { matchResult ->
                val hexColor = matchResult.groupValues[1]

                return@replace hexColor.replace(hexColor, "")
            }
        }

        /**
         * Parse color in string
         *
         * @return Changed string
         */
        fun String.parseColor(): String = Color.translate(this)!!

        /**
         * Strip colors in string
         *
         * @return Changed string
         */
        fun String.stripColor(): String = Color.strip(this)!!
    }

    private fun interface ColorExecutable {
        fun run(ansi: Ansi): String
    }
}