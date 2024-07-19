//package net.unix.api.player
//
//import net.unix.api.service.CloudService
//import net.unix.api.command.sender.CloudCommandSender
//import net.unix.api.player.exception.PlayerValueNotExistException
//import net.unix.api.chimera.client.Client
//import net.unix.api.chimera.server.Server
//
///**
// * [CloudService] player
// *
// * If the documentation says that a function can only accept primitives or strings, that's not really true.
// *
// * You can specify your own object class that you want to receive in the [Server] or [Client] class or write your own adapter
// */
//interface CloudPlayer : CloudCommandSender {
//
//    /**
//     * Get field value from service player class
//     *
//     * @param T Object type
//     * @param name Field name
//     * @param methods Call methods after field getting
//     *
//     * @return Field value
//     *
//     * @throws PlayerValueNotExistException If field not found or null
//     */
//    @Throws(PlayerValueNotExistException::class)
//    fun <T> getField(name: String, vararg methods: String): T
//
//    /**
//     * Get field value from service player class
//     *
//     * @param field Built-in field
//     *
//     * @return Field value
//     *
//     * @throws PlayerValueNotExistException If field not found or null
//     */
//    @Throws(PlayerValueNotExistException::class)
//    fun getField(field: Field): Any
//
//    /**
//     * Change field value on service player class
//     *
//     * @param name Field name
//     * @param value Value, that will be setted. Only primitives and strings
//     *
//     * @throws PlayerValueNotExistException If field not found
//     */
//    @Throws(PlayerValueNotExistException::class)
//    fun setField(name: String, value: Any)
//
//    /**
//     * Call method from service player class
//     * (Only methods without params)
//     *
//     * @param T Returns object type. Only primitives and strings
//     * @param name Method name
//     * @param methods Call methods after getting method call result
//     *
//     * @return Method call result
//     *
//     * @throws PlayerValueNotExistException If method not found
//     */
//    @Throws(PlayerValueNotExistException::class)
//    fun <T> callMethod(name: String, vararg methods: String): T
//
//    /**
//     * Call method from service player class
//     *
//     * @param T Returns object type. Only primitives and strings
//     * @param name Method name
//     * @param params Method params. Only primitives and strings
//     * @param methods Call methods after getting method call result
//     *
//     * @return Method call result
//     *
//     * @throws PlayerValueNotExistException If method not found
//     */
//    @Throws(PlayerValueNotExistException::class)
//    fun <T> callMethod(name: String, params: List<Any>, methods: List<String>): T
//
//    /**
//     * Call method from service player class
//     *
//     * @param method Built-in method
//     *
//     * @return Method result
//     *
//     * @throws PlayerValueNotExistException If method not found
//     */
//    @Throws(PlayerValueNotExistException::class)
//    fun callMethod(method: Method): Any
//
//    // TODO Дописать поля и методы спигота, велосити, банжикорда, нуккита
//
//    /**
//     * Player class field on service
//     */
//    interface Field
//
//    /**
//     * Player class method on service
//     */
//    interface Method
//
//    /**
//     * Spigot fields for CraftPlayer class
//     */
//    enum class SpigotField(private val type: Class<*>, private val fieldName: String, vararg methods: String) : Field {
//        FIRST_PLAYED(Long::class.java, "firstPlayed"),
//        LAST_PLAYED(Long::class.java,"lastPlayed"),
//        HAS_PLAYED_BEFORE(Boolean::class.java, "hasPlayedBefore"),
//        CONSERVATION_TRACKER(String::class.java, "conversationTracker", "toString"),
//        CHANNELS(String::class.java, "channels", "toString"),
//        HIDDEN_PLAYERS(String::class.java, "hiddenPlayers", "toString"),
//        PLUGIN_WEAK_REFERENCES(String::class.java, "pluginWeakReferences", "toString"),
//        HASH(Int::class.java, "hash"),
//        HEALTH(Double::class.java, "health"),
//        SCALED_HEALTH(Boolean::class.java, "scaledHealth"),
//        HEALTH_SCALE(Double::class.java, "healthScale"),
//        PLAYER_LIST_HEADER(String::class.java, "playerListHeader", "toString"),
//        PLAYER_LIST_FOOTER(String::class.java, "playerListFooter", "toString")
//    }
//
//    /**
//     * Spigot methods for CraftPlayer class
//     */
//    enum class SpigotMethod(private val type: Class<*>, private val methodName: String, vararg methods: String) : Method
//
//    enum class VelocityField(private val type: Class<*>, private val fieldName: String, vararg methods: String) : Field
//    enum class VelocityMethod(private val type: Class<*>, private val methodName: String, vararg methods: String) : Method
//
//    enum class BungeeCordField(private val type: Class<*>, private val fieldName: String, vararg methods: String) : Field
//    enum class BungeeCordMethod(private val type: Class<*>, private val methodName: String, vararg methods: String) : Method
//
//    enum class NukkitField(private val type: Class<*>, private val fieldName: String, vararg methods: String) : Field
//    enum class NukkitMethod(private val type: Class<*>, private val methodName: String, vararg methods: String) : Method
//}