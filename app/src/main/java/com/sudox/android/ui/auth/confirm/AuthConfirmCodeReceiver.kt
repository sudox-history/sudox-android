package com.sudox.android.ui.auth.confirm

import android.arch.lifecycle.MutableLiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.sudox.android.common.SMS_RECEIVED
import com.sudox.android.common.helpers.SMS_CODE_MESSAGE_REGEX
import javax.inject.Inject

class AuthConfirmCodeReceiver @Inject constructor() : BroadcastReceiver() {

    var codeLiveData: MutableLiveData<String> = MutableLiveData()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != SMS_RECEIVED) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val founded = messages.findLast { it.messageBody.matches(SMS_CODE_MESSAGE_REGEX) } ?: return
        val code = founded.messageBody.replace("Sudox: ", "")

        // Return to view
        codeLiveData.postValue(code)
    }
}