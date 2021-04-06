package io.github.anvell.keemobile.domain.entity

import java.util.*

data class KeyGroup(
    override val uuid: UUID = UUID.randomUUID(),
    override val name: String,
    override val iconId: Int = 49,
    val iconData: ByteArray? = null,
    val customIconUuid: UUID? = null,
    val notes: String = "",
    val times: KeyDateTime? = null,
    val isExpanded: Boolean = false,
    val defaultAutoTypeSequence: String? = null,
    val enableAutoType: Boolean? = null,
    val enableSearching: Boolean? = null,
    val lastTopVisibleEntry: UUID? = null,
    val groups: MutableList<KeyGroup> = mutableListOf(),
    val entries: MutableList<KeyEntry> = mutableListOf()
): KeyDatabaseItem() {

    fun getEntryByTitle(title: String): KeyEntry? {
        for (entry in entries) {
            if (entry.name.equals(title, ignoreCase = true)) {
                return entry
            }
        }

        return null
    }

}
