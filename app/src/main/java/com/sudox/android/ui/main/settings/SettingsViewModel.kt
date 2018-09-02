package com.sudox.android.ui.main.settings

import android.arch.lifecycle.ViewModel
import com.sudox.android.common.repository.auth.AccountRepository
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class SettingsViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                            private val accountRepository: AccountRepository) : ViewModel() {

    fun disconnect() = protocolClient.disconnect()

    fun removeAllAccounts() = accountRepository.removeAccounts()
}