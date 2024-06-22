package net.unix.api.chimera.universe

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.unix.api.chimera.client.Client
import net.unix.api.chimera.server.Server
import java.io.Serializable
import java.util.UUID

/**
 * Класс пакета
 *
 * @param uuid Уникальный UUID пакета
 * @param date UNIX время отправки пакета
 * @param channel Название канала, по которому будет отправлено
 * @param string Строки пакета
 * @param char Символы пакета
 * @param boolean Булевые значения пакета
 * @param int Int пакета
 * @param long Long пакета
 * @param double Double пакета
 * @param float Float пакета
 */
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

    inline fun <reified T> getNamedArgument(name: String): T? {
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
        fun builder(): PacketBuilder = PacketBuilder()
    }

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

        fun asResponseFor(packet: Packet): PacketBuilder {
            this.uuid = packet.uuid

            return this
        }

        fun setChannel(channel: String): PacketBuilder {
            this.channel = channel
            return this
        }

        fun addString(vararg string: String): PacketBuilder {
            this.string.addAll(string)
            return this
        }

        fun addNamedString(vararg pairs: Pair<String, String>): PacketBuilder {
            this.namedString.putAll(pairs)
            return this
        }

        fun addChar(vararg char: Char): PacketBuilder {
            this.char.addAll(char.asList())
            return this
        }

        fun addNamedChar(vararg pairs: Pair<String, Char>): PacketBuilder {
            this.namedChar.putAll(pairs)
            return this
        }

        fun addBoolean(vararg boolean: Boolean): PacketBuilder {
            this.boolean.addAll(boolean.asList())
            return this
        }

        fun addNamedBoolean(vararg pairs: Pair<String, Boolean>): PacketBuilder {
            this.namedBoolean.putAll(pairs)
            return this
        }

        fun addInt(vararg int: Int): PacketBuilder {
            this.int.addAll(int.asList())
            return this
        }

        fun addNamedInt(vararg pairs: Pair<String, Int>): PacketBuilder {
            this.namedInt.putAll(pairs)
            return this
        }

        fun addLong(vararg long: Long): PacketBuilder {
            this.long.addAll(long.asList())
            return this
        }

        fun addNamedLong(vararg pairs: Pair<String, Long>): PacketBuilder {
            this.namedLong.putAll(pairs)
            return this
        }

        fun addDouble(vararg double: Double): PacketBuilder {
            this.double.addAll(double.asList())
            return this
        }

        fun addNamedDouble(vararg pairs: Pair<String, Double>): PacketBuilder {
            this.namedDouble.putAll(pairs)
            return this
        }

        fun addFloat(vararg float: Float): PacketBuilder {
            this.float.addAll(float.asList())
            return this
        }

        fun addNamedFloat(vararg pairs: Pair<String, Float>): PacketBuilder {
            this.namedFloat.putAll(pairs)
            return this
        }

        fun onResponse(responsePacket: ResponsePacket): PacketBuilder {
            this.responsePacket = responsePacket
            return this
        }

        fun onResponseTimeout(timeout: Long, timeoutAction: TimeoutAction): PacketBuilder {
            this.responseTimeoutDelay = timeout
            this.responseTimeout = timeoutAction
            return this
        }

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