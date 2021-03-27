package io.github.anvell.keemobile.domain.entity

import java.util.*

abstract class KeyDatabaseItem {
    abstract val uuid: UUID
    abstract val name: String
    abstract val iconId: Int
}
