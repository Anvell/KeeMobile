package io.github.anvell.keemobile.domain.entity

import java.util.*

data class KeyMeta(
    val generator: String = "KeeMobile",
    val databaseName: String = "",
    val databaseDescription: String = "",
    val databaseNameChanged: Calendar? = null,
    val databaseDescriptionChanged: Calendar? = null,
    val defaultUserName: String = "",
    val defaultUserNameChanged: Calendar? = null,
    val maintenanceHistoryDays: Int = 365,
    val color: String? = null,
    val masterKeyChanged: Calendar? = null,
    val masterKeyChangeRec: Int = -1,
    val masterKeyChangeForce: Int = -1,
    val recycleBinUuid: UUID? = null,
    val recycleBinChanged: Calendar? = null,
    val recycleBinEnabled: Boolean = false,
    val entryTemplatesGroup: UUID? = null,
    val entryTemplatesGroupChanged: Calendar? = null,
    val lastSelectedGroup: UUID? = null,
    val lastTopVisibleGroup: UUID? = null,
    val historyMaxItems: Long = 10,
    val historyMaxSize: Long = 6291456,
    val customIcons: List<IconData>? = null,
    val binaries: List<BinaryData>? = null
)
