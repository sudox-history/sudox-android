package com.sudox.android.common.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sudox.android.common.enums.AuthHashState
import com.sudox.android.common.enums.EmailState
import com.sudox.android.common.enums.SignUpInState
import com.sudox.android.common.enums.State
import com.sudox.android.common.models.SendCodeData
import com.sudox.android.common.models.SignUpInData
import com.sudox.android.common.models.dto.*
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class AuthRepository @Inject constructor(private val protocolClient: ProtocolClient) {

    fun setSecret(secret: String?, id: String?) {
        if (secret != null && id != null) {
            protocolClient.id = id
            protocolClient.secret = secret
        }
    }

    fun sendEmail(email: String): LiveData<SendCodeData?> {
        val mutableLiveData = MutableLiveData<SendCodeData?>()

        if (protocolClient.isConnected()) {
            val sendCodeDTO = SendCodeDTO()
            sendCodeDTO.email = email

            protocolClient.makeRequest<SendCodeDTO>("auth.sendCode", sendCodeDTO) {
                when {
                    it.errorCode == 0 -> mutableLiveData.postValue(SendCodeData(EmailState.FAILED))
                    it.errorCode == 2 -> mutableLiveData.postValue(SendCodeData(EmailState.WRONG_FORMAT))
                    else -> mutableLiveData.postValue(SendCodeData(EmailState.SUCCESS, it.hash, it.status))
                }
            }
        } else {
            mutableLiveData.postValue(null)
        }

        return mutableLiveData
    }

    fun sendCode(code: String): LiveData<State?> {
        val mutableLiveData = MutableLiveData<State?>()

        if (protocolClient.isConnected()) {
            val confirmCodeDTO = ConfirmCodeDTO()
            confirmCodeDTO.code = code.toInt()

            protocolClient.makeRequest<ConfirmCodeDTO>("auth.confirmCode", confirmCodeDTO) {
                if (it.errorCode == 204) {
                    mutableLiveData.postValue(State.FAILED)
                } else if (it.codeStatus == 1) {
                    mutableLiveData.postValue(State.SUCCESS)
                }
            }

            return mutableLiveData
        }
        mutableLiveData.postValue(null)
        return mutableLiveData
    }

    fun sendCodeAgain(): LiveData<State?> {
        val mutableLiveData = MutableLiveData<State?>()

        if (protocolClient.isConnected()) {
            protocolClient.makeRequest<ResendDTO>("auth.resendCode", ResendDTO()) {
                if (it.errorCode == 203)
                    mutableLiveData.postValue(State.FAILED)
                else if (it.code == 1)
                    mutableLiveData.postValue(State.SUCCESS)

            }
            return mutableLiveData
        }
        mutableLiveData.postValue(null)
        return mutableLiveData
    }

    fun signUp(name: String, nickname: String): LiveData<SignUpInData?> {
        val mutableLiveData = MutableLiveData<SignUpInData?>()

        if (protocolClient.isConnected()) {
            val signUpDTO = SignUpDTO()
            signUpDTO.name = name
            signUpDTO.nickname = nickname

            protocolClient.makeRequest<SignUpDTO>("auth.signUp", signUpDTO) {
                when {
                    it.errorCode == 0 -> mutableLiveData.postValue(SignUpInData(SignUpInState.FAILED))
                    it.errorCode == 2 -> mutableLiveData.postValue(SignUpInData(SignUpInState.WRONG_FORMAT))
                    it.errorCode == 205 -> mutableLiveData.postValue(SignUpInData(SignUpInState.ACCOUNT_EXISTS))
                    else -> mutableLiveData.postValue(SignUpInData(SignUpInState.SUCCESS, it.id, it.secret))
                }
            }
            return mutableLiveData
        }

        mutableLiveData.postValue(null)
        return mutableLiveData
    }

    fun signIn(code: String): LiveData<SignUpInData?> {
        val mutableLiveData = MutableLiveData<SignUpInData?>()

        if (protocolClient.isConnected()) {
            val signInDTO = SignInDTO()
            signInDTO.code = code.toInt()

            protocolClient.makeRequest<SignInDTO>("auth.signIn", signInDTO) {
                when {
                    it.errorCode == 0 -> mutableLiveData.postValue(SignUpInData(SignUpInState.FAILED))
                    it.errorCode == 50 -> mutableLiveData.postValue(SignUpInData(SignUpInState.WRONG_FORMAT))
                    it.errorCode == 204 -> mutableLiveData.postValue(SignUpInData(SignUpInState.FAILED))
                    it.errorCode == 206 -> mutableLiveData.postValue(SignUpInData(SignUpInState.ACCOUNT_ERROR))
                    else -> mutableLiveData.postValue(SignUpInData(SignUpInState.SUCCESS, it.id, it.secret))
                }
            }
            return mutableLiveData
        }
        mutableLiveData.postValue(null)
        return mutableLiveData
    }

    fun importAuthHash(hash: String): LiveData<AuthHashState> {
        val mutableLiveData = MutableLiveData<AuthHashState>()

        val authHashDTO = AuthHashDTO()
        authHashDTO.hash = hash

        protocolClient.makeRequest<AuthHashDTO>("auth.restore", authHashDTO) {
            when {
                it.errorCode == 0 -> mutableLiveData.postValue(AuthHashState.FAILED)
                it.errorCode == 202 -> mutableLiveData.postValue(AuthHashState.DEAD)
                it.code == 0 -> mutableLiveData.postValue(AuthHashState.DEAD)
                else -> mutableLiveData.postValue(AuthHashState.ALIVE)
            }
        }
        return mutableLiveData
    }

    fun logOut(): LiveData<State> {
        val mutableLiveData = MutableLiveData<State>()

        protocolClient.makeRequest<SimpleAnswerDTO>("account.logOut", SimpleAnswerDTO()){
            if(it.response == 1){
                mutableLiveData.postValue(State.SUCCESS)
            }
        }
        return mutableLiveData
    }
}