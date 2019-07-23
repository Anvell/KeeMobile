package io.github.anvell.keemobile.domain.entity

import java.util.*

data class KeyEntry(
    val uuid: UUID = UUID.randomUUID(),
    val title: String,
    val username: String,
    val password: String,
    val url: String,
    val notes: String,
    val iconId: Int,
    val iconData: ByteArray?,
    val customIconUuid: UUID?,
    val times: KeyDateTime?,
    val foregroundColor: String?,
    val backgroundColor: String?,
    val overrideUrl: String,
    val autoType: KeyAutoType,
    val tags: MutableList<String> = mutableListOf(),
    val history: MutableList<KeyEntry> = mutableListOf(),
    val attachments: MutableList<KeyAttachment> = mutableListOf(),
    val customProperties: MutableList<KeyProperty> = mutableListOf()
) {

    fun getCustomPropertyByName(name: String): KeyProperty? {
        for (property in customProperties) {
            if (property.key.equals(name, ignoreCase = true)) {
                return property
            }
        }

        return null
    }

}
