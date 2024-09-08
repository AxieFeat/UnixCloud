package net.unix.api.network.universe

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.unix.api.network.client.Client
import net.unix.api.network.server.Server
import java.io.Serializable
import java.util.*

/**
 * Packet class
 *
 * @param uuid Packet UUID
 * @param date Time of send in millis
 * @param channel Channel name
 *
 * @param string String in packet
 * @param char Char in packet
 * @param boolean Boolean in packet
 * @param int Int in packet
 * @param long Long in packet
 * @param double Double in packet
 * @param float Float in packet
 *
 * @param namedString Named string in packet
 * @param namedChar Named char in packet
 * @param namedBoolean Named boolean in packet
 * @param namedInt Named int in packet
 * @param namedLong Named long in packet
 * @param namedDouble Named double in packet
 * @param namedFloat Named float in packet
 */
@Suppress("unused")
data class Packet(
    var uuid: String? = null,
    var date: Long? = null,
    var channel: String? = null,
    var string: List<String>? = null,
    var char: List<Char>? = null,
    var boolean: List<Boolean>? = null,
    var int: List<Int>? = null,
    var long: List<Long>? = null,
    var double: List<Double>? = null,
    var float: List<Float>? = null,
    var namedString: Map<String, String>? = null,
    var namedChar: Map<String, Char>? = null,
    var namedBoolean: Map<String, Boolean>? = null,
    var namedInt: Map<String, Int>? = null,
    var namedLong: Map<String, Long>? = null,
    var namedDouble: Map<String, Double>? = null,
    var namedFloat: Map<String, Float>? = null
) : Serializable {

    /**
     * Get named argument by name
     *
     * @param T Type of argument (Only primitives)
     * @param name Argument name
     *
     * @return Argument by name or null, if not founded
     */
    inline operator fun <reified T> get(name: String): T? {
        return when(T::class) {
            String::class -> {
                val value = namedString?.get(name) ?: return null

                return value as T
            }

            Char::class -> {
                val value = namedChar?.get(name) ?: return null

                return value as T
            }

            Boolean::class -> {
                val value = namedBoolean?.get(name) ?: return null

                return value as T
            }

            Int::class -> {
                val value = namedInt?.get(name) ?: return null

                return value as T
            }

            Long::class -> {
                val value = namedLong?.get(name) ?: return null

                return value as T
            }

            Double::class -> {
                val value = namedDouble?.get(name) ?: return null

                return value as T
            }

            Float::class -> {
                val value = namedFloat?.get(name) ?: return null

                return value as T
            }

            else -> null
        }
    }

    companion object {
        /**
         * Builder for packets
         */
        fun builder(): PacketBuilder = PacketBuilder()
    }

    /**
     * Simple builder for packets
     */
    class PacketBuilder {

        private var uuid: String? = null
        private lateinit var channel: String
        private val string: MutableList<String> = mutableListOf()
        private val char: MutableList<Char> = mutableListOf()
        private val boolean: MutableList<Boolean> = mutableListOf()
        private val int: MutableList<Int> = mutableListOf()
        private val long: MutableList<Long> = mutableListOf()
        private val double: MutableList<Double> = mutableListOf()
        private val float: MutableList<Float> = mutableListOf()

        private val namedString: MutableMap<String, String> = mutableMapOf()
        private val namedChar: MutableMap<String, Char> = mutableMapOf()
        private val namedBoolean: MutableMap<String, Boolean> = mutableMapOf()
        private val namedInt: MutableMap<String, Int> = mutableMapOf()
        private val namedLong: MutableMap<String, Long> = mutableMapOf()
        private val namedDouble: MutableMap<String, Double> = mutableMapOf()
        private val namedFloat: MutableMap<String, Float> = mutableMapOf()

        private var responsePacket: ResponsePacket? = null

        private var responseTimeoutDelay: Long? = null
        private var responseTimeout: TimeoutAction? = null

        /**
         * Mark packet as response for other packet
         *
         * @param packet On what packet response
         *
         * @return Current instance of [PacketBuilder]
         */
        fun asResponseFor(packet: Packet): PacketBuilder {
            this.uuid = packet.uuid

            return this
        }

        /**
         * Set channel for packet
         *
         * @param channel Channel name
         *
         * @return Current instance of [PacketBuilder]
         */
        fun setChannel(channel: String): PacketBuilder {
            this.channel = channel
            return this
        }

        /**
         * Add string to packet
         *
         * @param string String to add
         *
         * @return Current instance of [PacketBuilder]
         */
        fun addString(vararg string: String): PacketBuilder {
            this.string.addAll(string)
            return this
        }

        /**
         * Add named string to packet
         *
         * @param pair Pair of named argument. Key - argument name, Value - some argument value
         *
         * @return Current instance of [PacketBuilder]
         */
        fun addNamedString(vararg pair: Pair<String, String>): PacketBuilder {
            this.namedString.putAll(pair)
            return this
        }

        /**
         * Add char to packet
         *
         * @param char Char to add
         *
         * @return Current instance of [PacketBuilder]
         */
        fun addChar(vararg char: Char): PacketBuilder {
            this.char.addAll(char.asList())
            return this
        }

        /**
         * Add named char to packet
         *
         * @param pair Pair of named argument. Key - argument name, Value - some argument value
         *
         * @return Current instance of [PacketBuilder]
         */
        fun addNamedChar(vararg pair: Pair<String, Char>): PacketBuilder {
            this.namedChar.putAll(pair)
            return this
        }

        /**
         * Add boolean to packet
         *
         * @param boolean Boolean to add
         *
         * @return Current instance of [PacketBuilder]
         */
        fun addBoolean(vararg boolean: Boolean): PacketBuilder {
            this.boolean.addAll(boolean.asList())
            return this
        }

        /**
         * Add named boolean to packet
         *
         * @param pair Pair of named argument. Key - argument name, Value - some argument value
         *
         * @return Current instance of [PacketBuilder]
         */
        fun addNamedBoolean(vararg pair: Pair<String, Boolean>): PacketBuilder {
            this.namedBoolean.putAll(pair)
            return this
        }

        /**
         * Add int to packet
         *
         * @param int Int to add
         *
         * @return Current instance of [PacketBuilder]
         */
        fun addInt(vararg int: Int): PacketBuilder {
            this.int.addAll(int.asList())
            return this
        }

        /**
         * Add named int to packet
         *
         * @param pair Pair of named argument. Key - argument name, Value - some argument value
         *
         * @return Current instance of [PacketBuilder]
         */
        fun addNamedInt(vararg pair: Pair<String, Int>): PacketBuilder {
            this.namedInt.putAll(pair)
            return this
        }

        /**
         * Add long to packet
         *
         * @param long Long to add
         *
         * @return Current instance of [PacketBuilder]
         */
        fun addLong(vararg long: Long): PacketBuilder {
            this.long.addAll(long.asList())
            return this
        }

        /**
         * Add named long to packet
         *
         * @param pair Pair of named argument. Key - argument name, Value - some argument value
         *
         * @return Current instance of [PacketBuilder]
         */
        fun addNamedLong(vararg pair: Pair<String, Long>): PacketBuilder {
            this.namedLong.putAll(pair)
            return this
        }

        /**
         * Add double to packet
         *
         * @param double Double to add
         *
         * @return Current instance of [PacketBuilder]
         */
        fun addDouble(vararg double: Double): PacketBuilder {
            this.double.addAll(double.asList())
            return this
        }

        /**
         * Add named double to packet
         *
         * @param pair Pair of named argument. Key - argument name, Value - some argument value
         *
         * @return Current instance of [PacketBuilder]
         */
        fun addNamedDouble(vararg pair: Pair<String, Double>): PacketBuilder {
            this.namedDouble.putAll(pair)
            return this
        }

        fun addFloat(vararg float: Float): PacketBuilder {
            this.float.addAll(float.asList())
            return this
        }

        /**
         * Add named float to packet
         *
         * @param pair Pair of named argument. Key - argument name, Value - some argument value
         *
         * @return Current instance of [PacketBuilder]
         */
        fun addNamedFloat(vararg pair: Pair<String, Float>): PacketBuilder {
            this.namedFloat.putAll(pair)
            return this
        }

        /**
         * Mark packet as "response required"
         *
         * @param response Response action
         *
         * @return Current instance of [PacketBuilder]
         */
        fun onResponse(response: ResponsePacket): PacketBuilder {
            this.responsePacket = response
            return this
        }

        /**
         * Execute action, if packet not receive response. Works only if [onResponse] is set
         *
         * @param timeout Timeout in millis
         * @param action Timeout action
         *
         * @return Current instance of [PacketBuilder]
         */
        fun onResponseTimeout(timeout: Long, action: TimeoutAction): PacketBuilder {
            this.responseTimeoutDelay = timeout
            this.responseTimeout = action
            return this
        }

        /**
         * Send packet by any instance of [Network]
         *
         * @param network Sender of packet
         *
         * @return Instance of sent [Packet]
         *
         * @see [Server]
         * @see [Client]
         */
        fun send(network: Network): Packet {

            val uuid = if (this.uuid == null) generateUUID(network) else this.uuid

            return runBlocking {

                val packet = Packet(
                    uuid,
                    System.currentTimeMillis(),
                    channel,
                    string, char, boolean, int, long, double, float,
                    namedString, namedChar, namedBoolean, namedInt, namedLong, namedDouble, namedFloat
                )

                val pair = Pair(uuid!!, responsePacket!!)

                if (responsePacket != null) {
                    when (network) {
                        is Client -> network.waitingPacketListener.waitingPackets.add(pair)
                        is Server -> network.waitingPacketListener.waitingPackets.add(pair)
                    }

                    if (responseTimeoutDelay != null) {
                        launch {
                            delay(responseTimeoutDelay!!)

                            when (network) {
                                is Client -> {
                                    if (network.waitingPacketListener.waitingPackets.contains(pair)) {
                                        responseTimeout!!.run()
                                    }
                                }

                                is Server -> {
                                    if (network.waitingPacketListener.waitingPackets.contains(pair)) {
                                        responseTimeout!!.run()
                                    }
                                }
                            }
                        }
                    }
                }

                network.sendPacket(packet)

                return@runBlocking packet
            }
        }

        /**
         * Send packet by any instance of [Network]
         *
         * @param id Client id (For sending from [Server])
         * @param network Sender of packet
         *
         * @return Instance of sent [Packet]
         *
         * @see [Server]
         * @see [Client]
         */
        fun send(id: Int, network: Network): Packet {

            val uuid = if (this.uuid == null) generateUUID(network) else this.uuid

            return runBlocking {

                val packet = Packet(
                    uuid,
                    System.currentTimeMillis(),
                    channel,
                    string, char, boolean, int, long, double, float,
                    namedString, namedChar, namedBoolean, namedInt, namedLong, namedDouble, namedFloat
                )

                val pair = Pair(uuid!!, responsePacket!!)

                if (responsePacket != null) {
                    when (network) {
                        is Client -> network.waitingPacketListener.waitingPackets.add(pair)
                        is Server -> network.waitingPacketListener.waitingPackets.add(pair)
                    }

                    if (responseTimeoutDelay != null) {
                        launch {
                            delay(responseTimeoutDelay!!)

                            when (network) {
                                is Client -> {
                                    if (network.waitingPacketListener.waitingPackets.contains(pair)) {
                                        responseTimeout!!.run()
                                    }
                                }

                                is Server -> {
                                    if (network.waitingPacketListener.waitingPackets.contains(pair)) {
                                        responseTimeout!!.run()
                                    }
                                }
                            }
                        }
                    }
                }

                network.sendPacket(id, packet)

                return@runBlocking packet
            }
        }

        companion object  {
            private fun generateUUID(network: Network): String {
                val uuid = UUID.randomUUID().toString()

                val waitingPackets = when(network) {
                    is Client -> network.waitingPacketListener.waitingPackets
                    is Server -> network.waitingPacketListener.waitingPackets

                    else -> mutableListOf()
                }

                if (waitingPackets.any { it.first == uuid }) return generateUUID(network)

                return uuid
            }
        }
    }
}