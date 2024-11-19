@file:Suppress("unused")

package net.unix.api.persistence

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
interface PersistentDataType<T, Z> {

    /**
     * Returns the primitive data type of this tag.
     *
     * @return The class.
     */
    val primitiveType: Class<T>

    /**
     * Returns the complex object type the primitive value resembles.
     *
     * @return The class type.
     */
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
    fun toPrimitive(complex: Z, context: PersistentDataAdapterContext): T

    /**
     * Creates a complex object based of the passed primitive value.
     *
     * @param primitive The primitive value.
     * @param context The context this operation is running in.
     *
     * @return The complex object instance.
     */
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
    class PrimitivePersistentDataType<T> internal constructor(override val primitiveType: Class<T>) : PersistentDataType<T, T> {

        override val complexType: Class<T>
            get() = primitiveType

        override fun toPrimitive(complex: T, context: PersistentDataAdapterContext): T {
            return complex
        }

        override fun fromPrimitive(primitive: T, context: PersistentDataAdapterContext): T {
            return primitive
        }

    }

    companion object {

        val BYTE: PersistentDataType<Byte, Byte> = PrimitivePersistentDataType(Byte::class.java)
        val SHORT: PersistentDataType<Short, Short> = PrimitivePersistentDataType(Short::class.java)
        val INTEGER: PersistentDataType<Int, Int> = PrimitivePersistentDataType(Int::class.java)
        val LONG: PersistentDataType<Long, Long> = PrimitivePersistentDataType(Long::class.java)
        val FLOAT: PersistentDataType<Float, Float> = PrimitivePersistentDataType(Float::class.java)
        val DOUBLE: PersistentDataType<Double, Double> = PrimitivePersistentDataType(Double::class.java)

        val STRING: PersistentDataType<String, String> = PrimitivePersistentDataType(String::class.java)

        val BYTE_ARRAY: PersistentDataType<ByteArray, ByteArray> = PrimitivePersistentDataType(ByteArray::class.java)
        val INTEGER_ARRAY: PersistentDataType<IntArray, IntArray> = PrimitivePersistentDataType(
            IntArray::class.java
        )
        val LONG_ARRAY: PersistentDataType<LongArray, LongArray> = PrimitivePersistentDataType(
            LongArray::class.java
        )

        /**
         * Complex Arrays.
         */
        val TAG_CONTAINER_ARRAY = PrimitivePersistentDataType(Array<PersistentDataContainer>::class.java)

        /**
         * Nested PersistentDataContainer.
         */
        val TAG_CONTAINER = PrimitivePersistentDataType(PersistentDataContainer::class.java)

    }

}
