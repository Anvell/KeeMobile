package io.github.anvell.keemobile.domain.entity

import java.util.*

data class KeyMeta (
    val generator: String,
    val databaseName: String,
    val databaseDescription: String,
    val databaseNameChanged: Calendar?,
    val databaseDescriptionChanged: Calendar?,
    val defaultUserName: String,
    val defaultUserNameChanged: Calendar?,
    val maintenanceHistoryDays: Int,
    val color: String?,
    val masterKeyChanged: Calendar?,
    val masterKeyChangeRec: Int,
    val masterKeyChangeForce: Int,
    val recycleBinUuid: UUID?,
    val recycleBinChanged: Calendar?,
    val recycleBinEnabled: Boolean,
    val entryTemplatesGroup: UUID?,
    val entryTemplatesGroupChanged: Calendar?,
    val lastSelectedGroup: UUID?,
    val lastTopVisibleGroup: UUID?,
    val historyMaxItems: Long,
    val historyMaxSize: Long,
    val customIcons: List<IconData>?,
    val binaries: List<BinaryData>?
)
