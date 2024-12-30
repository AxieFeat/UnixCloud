@file:Suppress("unused")

package net.unix.driver.persistence

import net.unix.api.persistence.PersistentDataContainer
import net.unix.api.persistence.PersistentDataType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.rmi.registry.Registry

@Suppress("UNCHECKED_CAST")
object RemotePersistenceDataType : KoinComponent {

    private val registry: Registry by inject(named("default"))

    private val KEY = PersistentDataType.PrimitivePersistentDataType::class.simpleName

    val BYTE: PersistentDataType<Byte, Byte> =
        registry.lookup("$KEY-BYTE") as PersistentDataType<Byte, Byte>

    val SHORT: PersistentDataType<Short, Short> =
        registry.lookup("$KEY-SHORT") as PersistentDataType<Short, Short>

    val INTEGER: PersistentDataType<Int, Int> =
        registry.lookup("$KEY-INTEGER") as PersistentDataType<Int, Int>

    val LONG: PersistentDataType<Long, Long> =
        registry.lookup("$KEY-LONG") as PersistentDataType<Long, Long>

    val FLOAT: PersistentDataType<Float, Float> =
        registry.lookup("$KEY-FLOAT") as PersistentDataType<Float, Float>

    val DOUBLE: PersistentDataType<Double, Double> =
        registry.lookup("$KEY-DOUBLE") as PersistentDataType<Double, Double>

    val STRING: PersistentDataType<String, String> =
        registry.lookup("$KEY-STRING") as PersistentDataType<String, String>

    val BYTE_ARRAY: PersistentDataType<ByteArray, ByteArray> =
        registry.lookup("$KEY-BYTE_ARRAY") as PersistentDataType<ByteArray, ByteArray>

    val INTEGER_ARRAY: PersistentDataType<IntArray, IntArray> =
        registry.lookup("$KEY-INTEGER_ARRAY") as PersistentDataType<IntArray, IntArray>

    val LONG_ARRAY: PersistentDataType<LongArray, LongArray> =
        registry.lookup("$KEY-LONG_ARRAY") as PersistentDataType<LongArray, LongArray>

    val TAG_CONTAINER_ARRAY: PersistentDataType<Array<PersistentDataContainer>, Array<PersistentDataContainer>> =
        registry.lookup("$KEY-TAG_CONTAINER_ARRAY") as PersistentDataType<Array<PersistentDataContainer>, Array<PersistentDataContainer>>

    val TAG_CONTAINER: PersistentDataType<PersistentDataContainer, PersistentDataContainer> =
        registry.lookup("$KEY-TAG_CONTAINER") as PersistentDataType<PersistentDataContainer, PersistentDataContainer>
}