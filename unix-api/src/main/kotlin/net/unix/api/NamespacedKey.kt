package net.unix.api

import com.google.common.base.Preconditions
import net.kyori.adventure.key.Key
import net.unix.api.modification.Modification
import java.util.*
import java.util.regex.Pattern

/**
 * Represents a String based key which consists of two components - a namespace
 * and a key.
 *
 * Namespaces may only contain lowercase alphanumeric characters, periods,
 * underscores, and hyphens.
 *
 * Keys may only contain lowercase alphanumeric characters, periods,
 * underscores, hyphens, and forward slashes.
 */
class NamespacedKey : Key {

    val namespace: String
    val key: String

    /**
     * Create a key in a specific namespace.
     *
     * @param namespace namespace
     * @param key key
     */
    @Deprecated("should never be used by modifications, for internal use only!!")
    constructor(namespace: String, key: String) {
        Preconditions.checkArgument(
            VALID_NAMESPACE.matcher(namespace).matches(),
            "Invalid namespace. Must be [a-z0-9._-]: %s",
            namespace
        )
        Preconditions.checkArgument(
            VALID_KEY.matcher(key).matches(),
            "Invalid key. Must be [a-z0-9/._-]: %s",
            key
        )

        this.namespace = namespace
        this.key = key

        val string = toString()
        Preconditions.checkArgument(string.length < 256, "NamespacedKey must be less than 256 characters", string)
    }

    /**
     * Create a key in the module's namespace.
     *
     * Namespaces may only contain lowercase alphanumeric characters, periods,
     * underscores, and hyphens.
     *
     * Keys may only contain lowercase alphanumeric characters, periods,
     * underscores, hyphens, and forward slashes.
     *
     * @param modification The modification to use for the namespace
     * @param key The key to create
     */
    constructor(modification: Modification, key: String) {
        this.namespace = modification.info.name.lowercase(Locale.ROOT)
        this.key = key.lowercase()

        Preconditions.checkArgument(
            VALID_NAMESPACE.matcher(this.namespace).matches(), "Invalid namespace. Must be [a-z0-9._-]: %s",
            this.namespace
        )
        Preconditions.checkArgument(
            VALID_KEY.matcher(this.key).matches(), "Invalid key. Must be [a-z0-9/._-]: %s",
            this.key
        )

        val string = toString()
        Preconditions.checkArgument(string.length < 256, "NamespacedKey must be less than 256 characters (%s)", string)
    }

    override fun toString(): String = "${this.namespace}:${this.key}"
    override fun namespace(): String = namespace
    override fun value(): String = key
    override fun asString(): String = toString()

    companion object {

        /**
         * The namespace representing all inbuilt keys.
         */
        const val UNIX: String = "unix"

        private val VALID_NAMESPACE: Pattern = Pattern.compile("[a-z0-9._-]+")
        private val VALID_KEY: Pattern = Pattern.compile("[a-z0-9/._-]+")

        /**
         * Return a new random key in the [.unix] namespace.
         *
         * @return New key
         */
        @Deprecated("should never be used by modules, for internal use only!!")
        fun randomKey(): NamespacedKey {
            return NamespacedKey(UNIX, UUID.randomUUID().toString())
        }

        /**
         * Get a key in the Unix namespace.
         *
         * @param key The key to use
         *
         * @return New key in the Unix namespace
         */
        fun unix(key: String): NamespacedKey {
            return NamespacedKey(UNIX, key)
        }

        /**
         * Get a NamespacedKey from the supplied string with a default namespace if
         * a namespace is not defined. This is a utility method meant to fetch a
         * NamespacedKey from user input. Please note that casing does matter and
         * any instance of uppercase characters will be considered invalid.
         *
         * @param string The string to convert to a NamespacedKey
         *
         * @param defaultNamespace The default namespace to use if none was supplied.
         *
         * @return The created NamespacedKey. Null if invalid key
         */
        fun fromString(string: String, defaultNamespace: Modification?): NamespacedKey? {
            val components = string.split(":".toRegex(), limit = 3).toTypedArray()
            if (components.size > 2) {
                return null
            }

            val key = if ((components.size == 2)) components[1] else ""
            if (components.size == 1) {
                val value = components[0]
                if (value.isEmpty() || !VALID_KEY.matcher(value).matches()) {
                    return null
                }

                return if ((defaultNamespace != null)) NamespacedKey(defaultNamespace, value) else unix(value)
            } else if (components.size == 2 && !VALID_KEY.matcher(key).matches()) {
                return null
            }

            val namespace = components[0]
            if (namespace.isEmpty()) {
                return if ((defaultNamespace != null)) NamespacedKey(defaultNamespace, key) else unix(key)
            }

            if (!VALID_KEY.matcher(namespace).matches()) {
                return null
            }

            return NamespacedKey(namespace, key)
        }

        /**
         * Get a NamespacedKey from the supplied string.
         *
         * The default namespace will be Unix's
         *
         * @param key The key to convert to a NamespacedKey
         *
         * @return The created NamespacedKey. Null if invalid
         */
        fun fromString(key: String): NamespacedKey? {
            return fromString(key, null)
        }
    }
}