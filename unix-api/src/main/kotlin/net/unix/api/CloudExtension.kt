package net.unix.api

interface CloudExtension {

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
    fun String.parse(vararg args: Any): String

    /**
     * Парсит цвета в строке
     */
    fun String.parseColor(): String

    /**
     * Удаляет цвета из строки
     */
    fun String.stripColor(): String

    /**
     * Выводит объект в терминал
     */
    fun <T> T.print(): T
}