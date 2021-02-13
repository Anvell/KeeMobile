package io.github.anvell.keemobile.presentation.open

import androidx.lifecycle.lifecycleScope
import io.github.anvell.keemobile.domain.datatypes.fold
import io.github.anvell.keemobile.domain.datatypes.map
import io.github.anvell.keemobile.domain.entity.FileListEntry
import io.github.anvell.keemobile.domain.entity.FileListEntrySecrets
import io.github.anvell.keemobile.domain.entity.KeyOnly
import io.github.anvell.keemobile.domain.entity.Secret
import io.github.anvell.keemobile.presentation.R
import kotlinx.coroutines.launch

internal fun OpenFragment.attemptUnlock() = viewModel.withState { state ->
    val secrets = state.selectedFile?.encryptedSecrets
    val password = requireBinding().password.text.toString()

    if (password.isNotEmpty() && secrets is FileListEntrySecrets.Some) {
        viewModel.openFromSource(
            state.selectedFile.fileSource,
            KeyOnly(Secret.Unencrypted(password))
        )
    } else {
        when (secrets) {
            is FileListEntrySecrets.Unspecified -> {
                biometricHelper.canAuthenticate().fold(
                    left = { unlockSelected(UseFingerprintSheet.Options.SKIP) },
                    right = { UseFingerprintSheet.show(parentFragmentManager) }
                )
            }
            is FileListEntrySecrets.Never -> unlockSelected(UseFingerprintSheet.Options.NEVER)
            is FileListEntrySecrets.Some -> {
                require(secrets.fileSecrets is KeyOnly) { "Only password protection is supported." }

                lifecycleScope.launch {
                    val unencryptedSecret = biometricHelper.authenticateAndDecrypt(
                        fragment = this@attemptUnlock,
                        secret = (secrets.fileSecrets as KeyOnly).masterKey as Secret.Encrypted,
                        title = getString(R.string.open_dialogs_biometrics_title_unlock),
                        cancelLabel = getString(R.string.open_dialogs_biometrics_label_cancel)
                    )

                    unencryptedSecret.map { data ->
                        if (data != null) {
                            viewModel.openFromSource(
                                source = state.selectedFile.fileSource,
                                secrets = KeyOnly(data)
                            )
                        }
                    }
                }
            }
            else -> Unit
        }
    }
}

internal fun OpenFragment.unlockSelected(options: UseFingerprintSheet.Options) {
    viewModel.withState { state ->
        state.selectedFile?.fileSource?.let { selectedFileSource ->
            with(requireBinding()) {
                val masterKey = Secret.Unencrypted(password.text.toString())

                when (options) {
                    UseFingerprintSheet.Options.USE -> {
                        lifecycleScope.launch {
                            biometricHelper.authenticateAndEncrypt(
                                fragment = this@unlockSelected,
                                secret = masterKey,
                                title = getString(R.string.open_dialogs_biometrics_title_set),
                                cancelLabel = getString(R.string.open_dialogs_biometrics_label_cancel)
                            ).map { encryptedSecret ->
                                if (encryptedSecret != null) {
                                    viewModel.updateFileEntry(
                                        FileListEntry(
                                            fileSource = selectedFileSource,
                                            encryptedSecrets = FileListEntrySecrets.Some(
                                                KeyOnly(encryptedSecret)
                                            )
                                        )
                                    )
                                }
                            }

                            viewModel.openFromSource(
                                source = selectedFileSource,
                                secrets = KeyOnly(masterKey)
                            )
                        }
                    }
                    UseFingerprintSheet.Options.SKIP -> {
                        viewModel.updateFileEntry(
                            FileListEntry(
                                fileSource = selectedFileSource,
                                encryptedSecrets = FileListEntrySecrets.Unspecified()
                            )
                        )
                        viewModel.openFromSource(
                            source = selectedFileSource,
                            secrets = KeyOnly(masterKey)
                        )
                    }
                    UseFingerprintSheet.Options.NEVER -> {
                        viewModel.updateFileEntry(
                            FileListEntry(
                                fileSource = selectedFileSource,
                                encryptedSecrets = FileListEntrySecrets.Never()
                            )
                        )
                        viewModel.openFromSource(
                            source = selectedFileSource,
                            secrets = KeyOnly(masterKey)
                        )
                    }
                }
            }
        }
    }
}
