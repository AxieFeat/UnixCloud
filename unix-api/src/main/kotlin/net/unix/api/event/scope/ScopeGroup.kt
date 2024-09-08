package net.unix.api.event.scope

import java.util.regex.Pattern

/**
 * A scope group is a group of scopes that an event handler listens to. A scoped event has a specified scope (e.g.
 * 'A.B'). Every event handler, that listens for 'A.B', 'A.B.*' or a superior scope group like 'A.*' is able to receive
 * the event.
 *
 *
 * In general, a group must match the pattern [group]
 *
 *
 * This class is specifically for dispatching scoped events internally, but can also be used outside to manager scopes
 */
class ScopeGroup(scope: String) {
    private val group: Group?

    init {
        require(GROUP_PATTERN.matcher(scope).matches()) { "scope pattern $scope is not a valid scope group" }

        this.group = compile(scope)
    }

    /**
     * Determines whether a scope lies within this scope group.
     *
     * @param scope the scope that shall be checked.
     *
     * @return true, if the scope is within this group definition.
     */
    fun containsScope(scope: String): Boolean {
        require(!scope.contains("*")) { "Illegal scope format: $scope. A scope may not contain any wildcards" }

        val matcherGroup = compile(scope)
        return group!!.containsGroup(matcherGroup)
    }

    /**
     * A compiled scope group expression
     */
    private class Group

        /**
        * A scope group has a name and may have a subgroup.
        *
        * @param groupName name of the group or wildcard symbol "*".
        * @param subGroup  subgroup or null, if this is the deepest group already.
        */
        (private val groupName: String, private val subGroup: Group?) {

        /**
         * Recursive check wether another scope (group) is contained in this group.
         *
         * @param group the scope (group) that is checked.
         *
         * @return true, if the other group lies within this group.
         */
        fun containsGroup(group: Group?): Boolean {
            // if this is a wild card end of a group definition
            if (groupName.equals("*", ignoreCase = true)) {
                return true
            }


            // if sub group for the matcher group was provided and this isn't the wilcard end of a group def
            if (group == null) return false


            // if this the end of group def, but not a wildcard, a deeper group doesn't match
            if (this.subGroup == null) {
                return group.subGroup == null
            }

            return groupName.equals(group.groupName, ignoreCase = true) && subGroup.containsGroup(group.subGroup)
        }
    }

    companion object {
        private const val GROUP_REGEX = "((\\.?[a-zA-Z0-9])+(\\.\\*)?|\\*)"
        private val GROUP_PATTERN: Pattern = Pattern.compile(GROUP_REGEX)

        /**
         * Compiles the scope group, so it can be handled easier.
         */
        private fun compile(scopeString: String): Group? {
            val scopeGroupNames = scopeString.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()

            var newGroup: Group? = null
            for (i in scopeGroupNames.indices.reversed()) {
                newGroup = Group(scopeGroupNames[i], newGroup)
            }

            return newGroup
        }
    }
}