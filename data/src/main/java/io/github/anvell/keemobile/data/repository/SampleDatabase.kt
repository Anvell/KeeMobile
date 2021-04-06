package io.github.anvell.keemobile.data.repository

import io.github.anvell.keemobile.domain.entity.KeyDatabase
import io.github.anvell.keemobile.domain.entity.KeyEntry
import io.github.anvell.keemobile.domain.entity.KeyGroup
import io.github.anvell.keemobile.domain.entity.KeyMeta

internal val SampleDatabase = KeyDatabase(
    KeyMeta(),
    KeyGroup(
        name = "MyDB",
        notes = "Some notes",
        groups = mutableListOf(
            KeyGroup(name = "Special", notes = "Lorem ipsum dolor")
        ), entries = mutableListOf(
            KeyEntry(
                name = "My entry",
                password = "GJKHEFJEH656"
            ),
            KeyEntry(
                name = "Second entry",
                password = "R#JFJDERFLL"
            )
        )
    )
)
