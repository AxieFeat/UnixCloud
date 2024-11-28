package net.unix.api.group

/**
 * This interface represents some behavior rule of group.
 *
 * @param T Type of value.
 */
interface CloudGroupRule<T> {

    /**
     * Group, that's use this rule.
     */
    val group: CloudGroup

    /**
     * This function will return [T]. It will be used for [update] function.
     *
     * @return Instance of [T].
     */
    fun get(): T

    /**
     * This functions calls, when rule is updated. You can control group from here.
     *
     * @param input Updated value.
     */
    fun update(input: T)

}