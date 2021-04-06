package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.keemobile.domain.datatypes.eitherCatch
import io.github.anvell.keemobile.domain.datatypes.mapLeft
import io.github.anvell.keemobile.domain.dispatchers.CoroutineDispatchers
import io.github.anvell.keemobile.domain.entity.BinaryData
import io.github.anvell.keemobile.domain.exceptions.DownloadsSaveException
import io.github.anvell.keemobile.domain.repository.DownloadsRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Reusable
class SaveAttachment @Inject constructor(
    private val dispatchers: CoroutineDispatchers,
    private val downloadsRepository: DownloadsRepository
) {

    // Raw data is already decompressed at this stage
    suspend operator fun invoke(name: String, binaryData: BinaryData) =
        withContext(dispatchers.io) {
            eitherCatch {
                downloadsRepository.writeToDownloads(name, binaryData.data)
            }.mapLeft {
                DownloadsSaveException(cause = it)
            }
        }
}
