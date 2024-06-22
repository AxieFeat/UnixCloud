package net.unix.api

import net.unix.api.terminal.Color
import java.util.regex.Pattern

/**
 * Парсит аргументы в строке.
 * функция заменяет {ЧИСЛО} на элемемент
 * из массива по его номеру начиная с единицы
 *
 * Например "Сейчас {1}".parse("солнечно")
 *
 * @param args Элементы для замены
 * @return Изменённая строка
 */
fun String.parse(vararg args: Any): String {
    val sb = StringBuilder()

    val pattern = Pattern.compile("\\{(\\d+)}")
    val matcher = pattern.matcher(this)

    var pos = 0

    while (matcher.find()) {
        sb.append(this.substring(pos, matcher.start()))

        val index = matcher.group(1).toInt()

        if (index > 0 && index <= args.size) {
            sb.append(args[index - 1])
        }

        pos = matcher.end()
    }

    sb.append(this.substring(pos))

    return sb.toString()
}

/**
 * Парсит цвета в строке
 */
fun String.parseColor(): String = Color.translate(this)!!

/**
 * Удаляет цвета из строки
 */
fun String.stripColor(): String = Color.strip(this)!!

/**
 * Выводит объект в терминал
 */
fun <T> T.print(): T {
    CloudAPI.instance.logger.info(this.toString())

    return this
}