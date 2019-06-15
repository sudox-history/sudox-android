package com.sudox.android.ui.main.settings

import androidx.lifecycle.ViewModel
import com.sudox.android.data.repositories.users.AccountRepository
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class SettingsViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                            private val accountRepository: AccountRepository) : ViewModel()