@file:Suppress("unused")

package net.unix.api.persistence

import net.unix.api.pattern.Nameable
import net.unix.api.remote.RemoteAccessible
import java.rmi.RemoteException
import kotlin.jvm.Throws

/**
 * This class represents an enum with a generic content type. It defines the
 * types a custom tag can have.
 *
 * This interface can be used to create your own custom
 * [PersistentDataType] with different complex types.
 *
 * @param T The primary object type that is stored in the given tag.
 * @param Z The retrieved object type when applying this tag type.
 */
interface PersistentDataType<T, Z> : Nameable, RemoteAccessible {

    /**
     * Name of persistent type.
     *
     * @return Persistent name.
     */
    @get:Throws(RemoteException::class)
    override val name: String

    /**
     * Returns the primitive data type of this tag.
     *
     * @return The class.
     */
    @get:Throws(RemoteException::class)
    val primitiveType: Class<T>

    /**
     * Returns the complex object type the primitive value resembles.
     *
     * @return The class type.
     */
    @get:Throws(RemoteException::class)
    val complexType: Class<Z>

    /**
     * Returns the primitive data that resembles the complex object passed to
     * this method.
     *
     * @param complex The complex object instance.
     * @param context The context this operation is running in.
     *
     * @return The primitive value.
     */
    @Throws(RemoteException::class)
    fun toPrimitive(complex: Z, context: PersistentDataAdapterContext): T

    /**
     * Creates a complex object based of the passed primitive value.
     *
     * @param primitive The primitive value.
     * @param context The context this operation is running in.
     *
     * @return The complex object instance.
     */
    @Throws(RemoteException::class)
    fun fromPrimitive(primitive: T, context: PersistentDataAdapterContext): Z

    /**
     * A default implementation that simply exists to pass on the retrieved or
     * inserted value to the next layer.
     *
     * This implementation does not add any kind of logic, but is used to
     * provide default implementations for the primitive types.
     *
     * @param T The generic type of the primitive objects.
     */
    class PrimitivePersistentDataType<T> internal constructor(
        override val name: String,
        override val primitiveType: Class<T>
    ) : PersistentDataType<T, T> {

        @get:Throws(RemoteException::class)
        override val complexType: Class<T>
            get() = primitiveType

        @Throws(RemoteException::class)
        override fun toPrimitive(complex: T, context: PersistentDataAdapterContext): T {
            return complex
        }

        @Throws(RemoteException::class)
        override fun fromPrimitive(primitive: T, context: PersistentDataAdapterContext): T {
            return primitive
        }

    }

    companion object {

        @get:Throws(RemoteException::class)
        val BYTE: PersistentDataType<Byte, Byte> = PrimitivePersistentDataType("BYTE", Byte::class.java)
        @get:Throws(RemoteException::class)
        val SHORT: PersistentDataType<Short, Short> = PrimitivePersistentDataType("SHORT", Short::class.java)
        @get:Throws(RemoteException::class)
        val INTEGER: PersistentDataType<Int, Int> = PrimitivePersistentDataType("INTEGER", Int::class.java)
        @get:Throws(RemoteException::class)
        val LONG: PersistentDataType<Long, Long> = PrimitivePersistentDataType("LONG", Long::class.java)
        @get:Throws(RemoteException::class)
        val FLOAT: PersistentDataType<Float, Float> = PrimitivePersistentDataType("FLOAT", Float::class.java)
        @get:Throws(RemoteException::class)
        val DOUBLE: PersistentDataType<Double, Double> = PrimitivePersistentDataType("DOUBLE", Double::class.java)

        @get:Throws(RemoteException::class)
        val STRING: PersistentDataType<String, String> = PrimitivePersistentDataType("STRING", String::class.java)

        @get:Throws(RemoteException::class)
        val BYTE_ARRAY: PersistentDataType<ByteArray, ByteArray> = PrimitivePersistentDataType("BYTE_ARRAY", ByteArray::class.java)
        @get:Throws(RemoteException::class)
        val INTEGER_ARRAY: PersistentDataType<IntArray, IntArray> = PrimitivePersistentDataType(
            "INTEGER_ARRAY",
            IntArray::class.java
        )
        @get:Throws(RemoteException::class)
        val LONG_ARRAY: PersistentDataType<LongArray, LongArray> = PrimitivePersistentDataType(
            "LONG_ARRAY",
            LongArray::class.java
        )

        /**
         * Complex Arrays.
         */
        @get:Throws(RemoteException::class)
        val TAG_CONTAINER_ARRAY = PrimitivePersistentDataType(
            "TAG_CONTAINER_ARRAY",
            Array<PersistentDataContainer>::class.java
        )

        /**
         * Nested PersistentDataContainer.
         */
        @get:Throws(RemoteException::class)
        val TAG_CONTAINER = PrimitivePersistentDataType(
            "TAG_CONTAINER",
            PersistentDataContainer::class.java
        )

    }

}
