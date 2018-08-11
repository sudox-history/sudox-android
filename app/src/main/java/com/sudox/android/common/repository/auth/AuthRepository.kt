package com.sudox.android.common.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sudox.android.common.enums.State
import com.sudox.android.common.enums.TokenState
import com.sudox.android.common.models.TokenData
import com.sudox.android.common.models.dto.*
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.model.ResponseCallback
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class AuthRepository @Inject constructor(private val protocolClient: ProtocolClient) {

    // Disposables list
    private var disposables: CompositeDisposable = CompositeDisposable()

    fun sendToken(token: String?): LiveData<TokenData> {
        val tokenData = MutableLiveData<TokenData>()

        if (token == null) {
            tokenData.postValue(TokenData(TokenState.MISSING))
        } else {

            val tokenDTO = TokenDTO()
            tokenDTO.token = token

            protocolClient.makeRequest("auth.importToken", tokenDTO, object : ResponseCallback<TokenDTO> {
                override fun onMessage(response: TokenDTO) {
                    if (response.code == 0)
                        tokenData.postValue(TokenData(TokenState.WRONG))
                    else tokenData.postValue(TokenData(TokenState.CORRECT, response.id))
                }
            })
        }
        return tokenData
    }

    fun sendEmail(email: String): LiveData<AuthSessionDTO?> {
        val mutableLiveData = MutableLiveData<AuthSessionDTO?>()

        if (protocolClient.isConnected()) {
            protocolClient.makeRequest("auth.sendCode", SendCodeDTO(email),
                    object : ResponseCallback<AuthSessionDTO> {
                        override fun onMessage(response: AuthSessionDTO) {
                            mutableLiveData.postValue(response)
                        }
                    })
        } else {
            mutableLiveData.postValue(null)
        }

        return mutableLiveData
    }


    fun sendCode(code: String): MutableLiveData<ConfirmCodeDTO?> {
        val mutableLiveData = MutableLiveData<ConfirmCodeDTO?>()

        if (protocolClient.isConnected()) {
            val confirmCodeDTO = ConfirmCodeDTO()
            confirmCodeDTO.code = code.toInt()

            protocolClient.makeRequest("auth.confirmCode", confirmCodeDTO, object : ResponseCallback<ConfirmCodeDTO> {
                override fun onMessage(response: ConfirmCodeDTO) {
                    mutableLiveData.postValue(response)
                }
            })

            return mutableLiveData
        }
        mutableLiveData.postValue(null)
        return mutableLiveData
    }

    fun sendCodeAgain(): MutableLiveData<State?> {
        val mutableLiveData = MutableLiveData<State?>()

        if (protocolClient.isConnected()) {
            protocolClient.makeRequest("auth.resendCode", ResendDTO(), object : ResponseCallback<ResendDTO> {
                override fun onMessage(response: ResendDTO) {
                    if (response.code == 0)
                        mutableLiveData.postValue(State.FAILED)
                    else
                        mutableLiveData.postValue(State.SUCCESS)
                }
            })
            return mutableLiveData
        }
        mutableLiveData.postValue(null)
        return mutableLiveData
    }

    fun signUp(name: String, surname: String): MutableLiveData<SignUpDTO?> {
        val mutableLiveData = MutableLiveData<SignUpDTO?>()

        if (protocolClient.isConnected()) {
            val signUpDTO = SignUpDTO()
            signUpDTO.name = name
            signUpDTO.surname = surname

            protocolClient.makeRequest("auth.signUp", signUpDTO, object : ResponseCallback<SignUpDTO> {
                override fun onMessage(response: SignUpDTO) {
                    mutableLiveData.postValue(response)
                }
            })
            return mutableLiveData
        }

        mutableLiveData.postValue(null)
        return mutableLiveData
    }

    fun signIn(code: String): MutableLiveData<SignInDTO?> {
        val mutableLiveData = MutableLiveData<SignInDTO?>()

        if (protocolClient.isConnected()) {
            val signInDTO = SignInDTO()
            signInDTO.code = code.toInt()

            protocolClient.makeRequest("auth.signIn", signInDTO, object : ResponseCallback<SignInDTO> {
                override fun onMessage(response: SignInDTO) {
                    mutableLiveData.postValue(response)
                }
            })
            return mutableLiveData
        }
        mutableLiveData.postValue(null)
        return mutableLiveData
    }

    fun importAuthHash(hash: String): MutableLiveData<AuthHashDTO> {
        val mutableLiveData = MutableLiveData<AuthHashDTO>()

        val authHashDTO = AuthHashDTO()
        authHashDTO.hash = hash

        protocolClient.makeRequest("auth.import", authHashDTO, object : ResponseCallback<AuthHashDTO> {
            override fun onMessage(response: AuthHashDTO) {
                mutableLiveData.postValue(response)
            }
        })

        return mutableLiveData
    }

    fun cleanDisposables() {
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }
}