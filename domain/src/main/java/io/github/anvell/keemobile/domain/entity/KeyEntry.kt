package io.github.anvell.keemobile.domain.entity

import java.util.*

data class KeyEntry(
    override val uuid: UUID = UUID.randomUUID(),
    override val name: String,
    override val iconId: Int = 0,
    val iconData: ByteArray? = null,
    val customIconUuid: UUID? = null,
    val username: String = "",
    val password: String = "",
    val url: String = "",
    val notes: String = "",
    val times: KeyDateTime? = null,
    val foregroundColor: String? = null,
    val backgroundColor: String? = null,
    val overrideUrl: String = "",
    val autoType: KeyAutoType? = null,
    val tags: MutableList<String> = mutableListOf(),
    val history: MutableList<KeyEntry> = mutableListOf(),
    val attachments: MutableList<KeyAttachment> = mutableListOf(),
    val customProperties: MutableList<KeyProperty> = mutableListOf()
): KeyDatabaseItem() {

    fun getCustomPropertyByName(name: String): KeyProperty? {
        for (property in customProperties) {
            if (property.key.equals(name, ignoreCase = true)) {
                return property
            }
        }

        return null
    }

}
