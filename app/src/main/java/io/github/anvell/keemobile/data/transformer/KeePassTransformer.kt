package io.github.anvell.keemobile.data.transformer

import de.slackspace.openkeepass.domain.*
import io.github.anvell.keemobile.domain.entity.*

private const val USER_NAME = "UserName"
private const val NOTES = "Notes"
private const val URL = "URL"
private const val PASSWORD = "Password"
private const val TITLE = "Title"

object KeePassTransformer {

    fun from(database: KeePassFile): KeyDatabase = KeyDatabase(
        from(database.meta),
        from(database.root.rootGroup),
        database.root?.deletedObjects?.map {
            KeyDeletedEntry(it.uuid, it.deletionTime)
        }?.toMutableList() ?: mutableListOf()
    )

    fun from(database: KeyDatabase): KeePassFile = KeePassFileBuilder(from(database.meta))
        .withRoot(
            DocumentRootBuilder()
                .rootGroup(from(database, database.root))
                .addDeletedObjects(database.deletedEntries.map {
                    DeletedObjectBuilder(it.uuid)
                        .deletionTime(it.deletionTime)
                        .build()
                })
                .build()
        ).build()

    fun from(sourceMeta: KeyMeta): Meta = with(sourceMeta) {
        MetaBuilder(databaseName)
            .generator(generator)
            .databaseDescription(databaseDescription)
            .databaseNameChanged(databaseNameChanged)
            .databaseDescriptionChanged(databaseDescriptionChanged)
            .defaultUserName(defaultUserName)
            .defaultUserNameChanged(defaultUserNameChanged)
            .maintenanceHistoryDays(maintenanceHistoryDays)
            .color(color)
            .masterKeyChanged(masterKeyChanged)
            .masterKeyChangeRec(masterKeyChangeRec)
            .masterKeyChangeForce(masterKeyChangeForce)
            .recycleBinUuid(recycleBinUuid)
            .recycleBinChanged(recycleBinChanged)
            .recycleBinEnabled(recycleBinEnabled)
            .entryTemplatesGroup(entryTemplatesGroup)
            .entryTemplatesGroupChanged(entryTemplatesGroupChanged)
            .lastSelectedGroup(lastSelectedGroup)
            .lastTopVisibleGroup(lastTopVisibleGroup)
            .historyMaxItems(historyMaxItems)
            .historyMaxSize(historyMaxSize)
            .binaries(
                BinariesBuilder()
                    .binaries(binaries?.map {
                        BinaryBuilder()
                            .id(it.id)
                            .data(it.data)
                            .isCompressed(it.isCompressed)
                            .build()
                    }).build()
            )
            .customIcons(CustomIconsBuilder().customIcons(
                customIcons?.map {
                    CustomIconBuilder()
                        .uuid(it.uuid)
                        .data(it.data)
                        .build()
                }
            ).build())
            .build()
    }

    fun from(database: KeyDatabase, sourceGroup: KeyGroup): Group = with(sourceGroup) {
        val groupBuilder = GroupBuilder(uuid)
            .name(name)
            .notes(notes)
            .iconId(iconId)
            .iconData(iconData)
            .customIconUuid(customIconUuid)
            .times(from(times))
            .isExpanded(isExpanded)
            .defaultAutoTypeSequence(defaultAutoTypeSequence)
            .enableAutoType(enableAutoType)
            .enableSearching(enableSearching)
            .lastTopVisibleEntry(lastTopVisibleEntry)
            .addEntries(entries.map { from(database, it) })

        groups.forEach {
            groupBuilder.addGroup(from(database, it))
        }

        groupBuilder.build()
    }

    fun from(sourceTimes: KeyDateTime?) = sourceTimes?.let {
        with(sourceTimes) {
            TimesBuilder()
                .creationTime(creationTime)
                .lastAccessTime(lastAccessTime)
                .lastModificationTime(lastModificationTime)
                .locationChanged(locationChanged)
                .expiryTime(expiryTime)
                .usageCount(usageCount)
                .expires(expires)
                .build()
        }
    }

    fun from(sourceTimes: Times?) = sourceTimes?.let {
        with(sourceTimes) {
            KeyDateTime(
                creationTime,
                lastAccessTime,
                lastModificationTime,
                locationChanged,
                expiryTime,
                usageCount,
                expires()
            )
        }
    }

    fun from(database: KeyDatabase, sourceEntry: KeyEntry): Entry = with(sourceEntry) {
        val entryBuilder = EntryBuilder()
            .uuid(uuid)
            .title(title)
            .username(username)
            .password(password)
            .url(url)
            .notes(notes)
            .iconId(iconId)
            .iconData(iconData)
            .customIconUuid(customIconUuid)
            .times(from(times))
            .foregroundColor(foregroundColor)
            .backgroundColor(backgroundColor)
            .overrideUrl(overrideUrl)
            .autoType(from(autoType))
            .tags(tags)

        if (history.size > 0) {
            val entryHistory = History()

            history.forEach {
                entryHistory.historicEntries.add(from(database, it))
            }
            entryBuilder.history(entryHistory)
        }

        entryBuilder.attachmentList.addAll(
            attachments.map { Attachment(it.key, it.ref, database.getAttachmentDataById(it.ref)) }
        )

        entryBuilder.customPropertyList.addAll(
            customProperties.map { Property(it.key, it.value, it.isProtected) }
        )

        entryBuilder.build()
    }

    fun from(sourceAutoType: KeyAutoType): AutoType = with(sourceAutoType) {
        AutoTypeBuilder()
            .enabled(isEnabled)
            .defaultSequence(defaultSequence)
            .dataTransferObfuscation(dataTransferObfuscation)
            .addAssociation(*associations.map {
                AutoTypeAssociationBuilder()
                    .windowTitle(it.windowTitle)
                    .keystrokeSequence(it.keystrokeSequence)
                    .build()
            }.toTypedArray())
            .build()
    }

    fun from(sourceMeta: Meta): KeyMeta = with(sourceMeta) {
        KeyMeta(
            generator ?: "",
            databaseName ?: "",
            databaseDescription ?: "",
            databaseNameChanged,
            databaseDescriptionChanged,
            defaultUserName ?: "",
            defaultUserNameChanged,
            maintenanceHistoryDays,
            color,
            masterKeyChanged,
            masterKeyChangeRec,
            masterKeyChangeForce,
            recycleBinUuid,
            recycleBinChanged,
            recycleBinEnabled,
            entryTemplatesGroup,
            entryTemplatesGroupChanged,
            lastSelectedGroup,
            lastTopVisibleGroup,
            historyMaxItems,
            historyMaxSize,
            customIcons.icons.map { IconData(it.uuid, it.data) },
            binaries.binaries.map { BinaryData(it.id, it.isCompressed, it.data) }
        )
    }

    fun from(sourceGroup: Group): KeyGroup = with(sourceGroup) {
        KeyGroup(
            uuid,
            name ?: "",
            notes ?: "",
            iconId,
            iconData,
            customIconUuid,
            from(times),
            isExpanded,
            defaultAutoTypeSequence,
            isEnableAutoType,
            isEnableSearching,
            lastTopVisibleEntry,
            groups.map { from(it) }.toMutableList(),
            entries.map { from(it) }.toMutableList()
        )
    }

    fun from(sourceEntry: Entry): KeyEntry = with(sourceEntry) {
        KeyEntry(
            uuid,
            getPropertyByName(TITLE)?.value ?: "",
            getPropertyByName(USER_NAME)?.value ?: "",
            getPropertyByName(PASSWORD)?.value ?: "",
            getPropertyByName(URL)?.value ?: "",
            getPropertyByName(NOTES)?.value ?: "",
            iconId,
            iconData,
            customIconUuid,
            from(times),
            foregroundColor,
            backgroundColor,
            overrideUrl ?: "",
            from(autoType),
            tags ?: mutableListOf(),
            history?.historicEntries?.map { from(it) }?.toMutableList() ?: mutableListOf(),
            attachments?.map { KeyAttachment(it.key, it.ref) }?.toMutableList() ?: mutableListOf(),
            customProperties?.map { KeyProperty(it.key, it.value, it.isProtected) }?.toMutableList() ?: mutableListOf()
        )
    }

    fun from(sourceAutoType: AutoType): KeyAutoType = with(sourceAutoType) {
        KeyAutoType(
            isEnabled,
            dataTransferObfuscation,
            defaultSequence,
            associations.map {
                KeyAutoTypeAssociation(it.windowTitle, it.keystrokeSequence)
            }.toMutableList()
        )
    }

}
