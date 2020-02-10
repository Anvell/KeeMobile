package io.github.anvell.keemobile.domain.usecase

import dagger.Reusable
import io.github.anvell.keemobile.common.extensions.scheduleOn
import io.github.anvell.keemobile.common.rx.RxSchedulers
import io.github.anvell.keemobile.domain.entity.BinaryData
import io.github.anvell.keemobile.domain.exceptions.DownloadsSaveException
import io.github.anvell.keemobile.domain.repository.DownloadsRepository
import io.reactivex.Single
import javax.inject.Inject

@Reusable
class SaveAttachment @Inject constructor(
    private val rxSchedulers: RxSchedulers,
    private val downloadsRepository: DownloadsRepository
) {

    // Raw data is already decompressed at this stage
    fun use(name: String, binaryData: BinaryData) = Single.fromCallable {

        try {
            downloadsRepository.writeToDownloads(name, binaryData.data)
        } catch (e: Exception) {
            throw DownloadsSaveException(cause = e)
        }
    }.scheduleOn(rxSchedulers.io())
}
