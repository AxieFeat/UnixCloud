package net.unix.api.player

import net.unix.api.command.sender.CloudCommandSender
import net.unix.api.player.exception.PlayerValueNotExistException

interface CloudPlayer : CloudCommandSender {

    /**
     * Получает значение из поля объекта на сервисе
     *
     * @param T тип объекта, который должен быть возвращён
     * @param name Название поля
     * @param methods Названия методов, который должны будут быть вызваны после получения результата на сервисе
     *
     * @return Указанный объект
     *
     * @throws PlayerValueNotExistException Если поле не найдено
     */
    @Throws(PlayerValueNotExistException::class)
    fun <T> getField(name: String, vararg methods: String): T

    /**
     * Получает значение из поля объекта на сервисе
     *
     * @param field Объект поля
     *
     * @return Указанный объект
     *
     * @throws PlayerValueNotExistException Если поле не найдено
     */
    @Throws(PlayerValueNotExistException::class)
    fun getField(field: Field): Any

    /**
     * Изменяет значение поля у объекта на сервисе
     *
     * @param name Название поля
     * @param value Значение, которое будет установлено, только примитивные типы данных или строки!
     *
     * @throws PlayerValueNotExistException Если поле не найдено
     */
    @Throws(PlayerValueNotExistException::class)
    fun setField(name: String, value: Any)

    /**
     * Вызывает метод с указанным названием на сервисе
     * (Только для методов без параметров)
     *
     * @param T тип объекта, который должен быть возвращён, возвращаемый объект должен быть примитивным типом данных или строкой!
     * @param name Название метода
     * @param methods Названия методов, который должны будут быть вызваны после получения результата на сервисе
     *
     * @return Указанный объект
     *
     * @throws PlayerValueNotExistException Если метод не найден
     */
    @Throws(PlayerValueNotExistException::class)
    fun <T> callMethod(name: String, vararg methods: String): T

    /**
     * Вызывает метод с указанным названием на сервисе
     * (Только для методов без параметров)
     *
     * @param T тип объекта, который должен быть возвращён, возвращаемый объект должен быть примитивным типом данных или строкой!
     * @param name Название метода
     * @param params Параметры метода, только примитивы или строки!
     * @param methods Названия методов, который должны будут быть вызваны после получения результата на сервисе
     *
     * @return Указанный объект
     *
     * @throws PlayerValueNotExistException Если метод не найден
     */
    @Throws(PlayerValueNotExistException::class)
    fun <T> callMethod(name: String, params: List<String>, methods: List<String>): T

    /**
     * Вызывает метод с указанным названием на сервисе
     * (Только для методов без параметров)
     *
     * @param method Объект метода
     *
     * @return Указанный объект
     *
     * @throws PlayerValueNotExistException Если метод не найден
     */
    @Throws(PlayerValueNotExistException::class)
    fun callMethod(method: Method): Any

    // TODO Дописать поля и методы спигота, велосити, банжикорда, нуккита

    interface Field
    interface Method

    enum class SpigotField(private val type: Class<*>, private val fieldName: String, vararg methods: String) : Field {
        FIRST_PLAYED(Long::class.java, "firstPlayed"),
        LAST_PLAYED(Long::class.java,"lastPlayed"),
        HAS_PLAYED_BEFORE(Boolean::class.java, "hasPlayedBefore"),
        CONSERVATION_TRACKER(String::class.java, "conversationTracker", "toString"),
        CHANNELS(String::class.java, "channels", "toString"),
        HIDDEN_PLAYERS(String::class.java, "hiddenPlayers", "toString"),
        PLUGIN_WEAK_REFERENCES(String::class.java, "pluginWeakReferences", "toString"),
        HASH(Int::class.java, "hash"),
        HEALTH(Double::class.java, "health"),
        SCALED_HEALTH(Boolean::class.java, "scaledHealth"),
        HEALTH_SCALE(Double::class.java, "healthScale"),
        PLAYER_LIST_HEADER(String::class.java, "playerListHeader", "toString"),
        PLAYER_LIST_FOOTER(String::class.java, "playerListFooter", "toString")
    }

    enum class SpigotMethod(private val type: Class<*>, private val methodName: String, vararg methods: String) : Method

    enum class VelocityField(private val type: Class<*>, private val fieldName: String, vararg methods: String) : Field
    enum class VelocityMethod(private val type: Class<*>, private val methodName: String, vararg methods: String) : Method

    enum class BungeeCordField(private val type: Class<*>, private val fieldName: String, vararg methods: String) : Field
    enum class BungeeCordMethod(private val type: Class<*>, private val methodName: String, vararg methods: String) : Method

    enum class NukkitField(private val type: Class<*>, private val fieldName: String, vararg methods: String) : Field
    enum class NukkitMethod(private val type: Class<*>, private val methodName: String, vararg methods: String) : Method
}