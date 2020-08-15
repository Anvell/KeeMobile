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
        database.meta.convert(),
        database.root.rootGroup.convert(),
        database.root?.deletedObjects?.map {
            KeyDeletedEntry(it.uuid, it.deletionTime)
        }?.toMutableList() ?: mutableListOf()
    )

    fun to(database: KeyDatabase): KeePassFile = KeePassFileBuilder(database.meta.convert())
        .withRoot(
            DocumentRootBuilder()
                .rootGroup(database.root.convert(database))
                .addDeletedObjects(database.deletedEntries.map {
                    DeletedObjectBuilder(it.uuid)
                        .deletionTime(it.deletionTime)
                        .build()
                })
                .build()
        ).build()

    private fun KeyMeta.convert(): Meta =
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

    private fun KeyGroup.convert(database: KeyDatabase): Group {
        val groupBuilder = GroupBuilder(uuid)
            .name(name)
            .notes(notes)
            .iconId(iconId)
            .iconData(iconData)
            .customIconUuid(customIconUuid)
            .times(times?.convert())
            .isExpanded(isExpanded)
            .defaultAutoTypeSequence(defaultAutoTypeSequence)
            .enableAutoType(enableAutoType)
            .enableSearching(enableSearching)
            .lastTopVisibleEntry(lastTopVisibleEntry)
            .addEntries(entries.map { it.convert(database) })

        groups.forEach {
            groupBuilder.addGroup(it.convert(database))
        }

        return groupBuilder.build()
    }

    private fun KeyDateTime.convert(): Times =
        TimesBuilder()
            .creationTime(creationTime)
            .lastAccessTime(lastAccessTime)
            .lastModificationTime(lastModificationTime)
            .locationChanged(locationChanged)
            .expiryTime(expiryTime)
            .usageCount(usageCount)
            .expires(expires)
            .build()

    private fun Times.convert(): KeyDateTime =
        KeyDateTime(
            creationTime,
            lastAccessTime,
            lastModificationTime,
            locationChanged,
            expiryTime,
            usageCount,
            expires()
        )

    private fun KeyEntry.convert(database: KeyDatabase): Entry {
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
            .times(times?.convert())
            .foregroundColor(foregroundColor)
            .backgroundColor(backgroundColor)
            .overrideUrl(overrideUrl)
            .autoType(autoType?.convert())
            .tags(tags)

        if (history.size > 0) {
            val entryHistory = History()

            history.forEach {
                entryHistory.historicEntries.add(it.convert(database))
            }
            entryBuilder.history(entryHistory)
        }

        entryBuilder.attachmentList.addAll(
            attachments.map { Attachment(it.key, it.ref, database.getAttachmentDataById(it.ref)) }
        )

        entryBuilder.customPropertyList.addAll(
            customProperties.map { Property(it.key, it.value, it.isProtected) }
        )

        return entryBuilder.build()
    }

    private fun KeyAutoType.convert(): AutoType =
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

    private fun Meta.convert(): KeyMeta =
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

    private fun Group.convert(): KeyGroup =
        KeyGroup(
            uuid,
            name ?: "",
            notes ?: "",
            iconId,
            iconData,
            customIconUuid,
            times?.convert(),
            isExpanded,
            defaultAutoTypeSequence,
            isEnableAutoType,
            isEnableSearching,
            lastTopVisibleEntry,
            groups.map { it.convert() }.toMutableList(),
            entries.map { it.convert() }.toMutableList()
        )

    private fun Entry.convert(): KeyEntry =
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
            times?.convert(),
            foregroundColor,
            backgroundColor,
            overrideUrl ?: "",
            autoType?.convert(),
            tags ?: mutableListOf(),
            history?.historicEntries?.map { it.convert() }?.toMutableList() ?: mutableListOf(),
            attachments?.map { KeyAttachment(it.key, it.ref) }?.toMutableList() ?: mutableListOf(),
            customProperties?.map { KeyProperty(it.key, it.value, it.isProtected) }?.toMutableList() ?: mutableListOf()
        )

    private fun AutoType.convert(): KeyAutoType =
        KeyAutoType(
            isEnabled,
            dataTransferObfuscation,
            defaultSequence,
            associations.map {
                KeyAutoTypeAssociation(it.windowTitle, it.keystrokeSequence)
            }.toMutableList()
        )

}
