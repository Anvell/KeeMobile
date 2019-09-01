package io.github.anvell.keemobile.domain.entity

import java.util.*

open class KeyDatabase(
    var meta: KeyMeta,
    var root: KeyGroup,
    var deletedEntries: MutableList<KeyDeletedEntry> = mutableListOf()
) {

    fun replaceEntry(entry: KeyEntry) {
        val result = findEntry { it.uuid.compareTo(entry.uuid) == 0 }

        result?.also { (parentGroup, foundEntry) ->
            val index = parentGroup.entries.indexOf(foundEntry)
            parentGroup.entries[index] = entry
        }
    }

    fun findEntry(predicate: (KeyEntry) -> Boolean): Pair<KeyGroup, KeyEntry>? {
        val stack = Stack<KeyGroup>()
        stack.push(root)

        while (!stack.empty()) {
            val group = stack.pop()

            for (entry in group.entries) {
                if (predicate(entry)) {
                    return group to entry
                }
            }

            if (group.groups.isNotEmpty()) {
                group.groups.forEach { stack.push(it) }
            }
        }

        return null
    }

    fun findGroup(predicate: (KeyGroup) -> Boolean): KeyGroup? {
        val stack = Stack<KeyGroup>()
        stack.push(root)

        while (!stack.empty()) {
            val group = stack.pop()

            if (predicate(group)) {
                return group
            }

            if (group.groups.isNotEmpty()) {
                group.groups.forEach { stack.push(it) }
            }
        }

        return null
    }

    fun getAttachmentDataById(id: Int): ByteArray? {
        val data = meta.binaries?.find { it.id == id }
        return data?.data
    }

}
