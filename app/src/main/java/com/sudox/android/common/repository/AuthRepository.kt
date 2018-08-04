package com.sudox.android.common.repository

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
            protocolClient.makeRequest("auth.importToken", TokenDTO(token), object : ResponseCallback<TokenDTO> {
                override fun onMessage(response: TokenDTO) {
                    if (response.code == 0)
                        tokenData.postValue(TokenData(TokenState.WRONG))
                    else tokenData.postValue(TokenData(TokenState.CORRECT, response.id))
                }
            })
        }
        return tokenData
    }

    fun sendEmail(email: String): LiveData<AuthSessionDTO> {
        val mutableLiveData = MutableLiveData<AuthSessionDTO>()

        protocolClient.makeRequest("auth.sendCode", SendCodeDTO(email), object : ResponseCallback<AuthSessionDTO> {
            override fun onMessage(response: AuthSessionDTO) {
                mutableLiveData.postValue(response)
            }
        })

        return mutableLiveData
    }


    fun sendCode(code: String): MutableLiveData<ConfirmCodeDTO> {
        val mutableLiveData = MutableLiveData<ConfirmCodeDTO>()

        val confirmCodeDTO = ConfirmCodeDTO()
        confirmCodeDTO.code = code.toInt()

        protocolClient.makeRequest("auth.confirmCode", confirmCodeDTO, object : ResponseCallback<ConfirmCodeDTO>{
            override fun onMessage(response: ConfirmCodeDTO) {
                mutableLiveData.postValue(response)
            }
        })

        return mutableLiveData
    }

    fun sendCodeAgain(): MutableLiveData<State> {
        val mutableLiveData = MutableLiveData<State>()

        protocolClient.makeRequest("auth.resendCode", ResendDTO(), object : ResponseCallback<ResendDTO>{
            override fun onMessage(response: ResendDTO) {
                if(response.code == 0)
                    mutableLiveData.postValue(State.FAILED)
                else
                    mutableLiveData.postValue(State.SUCCESS)
            }
        })
        return mutableLiveData
    }

    fun signUp(name: String, surname: String): MutableLiveData<SignUpDTO>{
        val mutableLiveData = MutableLiveData<SignUpDTO>()

        val signUpDTO = SignUpDTO()
        signUpDTO.name = name
        signUpDTO.surname = surname

        protocolClient.makeRequest("auth.signUp", signUpDTO, object : ResponseCallback<SignUpDTO>{
            override fun onMessage(response: SignUpDTO) {
                mutableLiveData.postValue(response)
            }
        })

        return mutableLiveData
    }

    fun signIn(code: String): MutableLiveData<SignInDTO> {
        val mutableLiveData = MutableLiveData<SignInDTO>()

        val signInDTO = SignInDTO()
        signInDTO.code = code.toInt()

        protocolClient.makeRequest("auth.signIn", signInDTO, object : ResponseCallback<SignInDTO>{
            override fun onMessage(response: SignInDTO) {
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