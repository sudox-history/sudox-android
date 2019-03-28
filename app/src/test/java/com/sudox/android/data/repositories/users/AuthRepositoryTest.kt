package com.sudox.android.data.repositories.users

import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.exceptions.RequestException
import com.sudox.android.data.exceptions.RequestRegexException
import com.sudox.android.data.models.auth.dto.*
import com.sudox.android.data.models.auth.state.AuthSession
import com.sudox.android.data.models.common.Errors
import com.sudox.protocol.models.dto.CoreVersionDTO
import com.sudox.android.data.models.users.dto.UserDTO
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.helpers.randomBase64String
import com.sudox.protocol.models.NetworkException
import com.sudox.protocol.models.ReadCallback
import com.sudox.protocol.models.enums.ConnectionState
import com.sudox.tests.helpers.any
import com.sudox.tests.helpers.eq
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.concurrent.ConcurrentLinkedDeque
import javax.inject.Provider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

@RunWith(PowerMockRunner::class)
@PrepareForTest(ConflatedBroadcastChannel::class, AccountRepository::class, AuthRepository::class, ProtocolClient::class, UsersRepository::class)
class AuthRepositoryTest : Assert() {

    private lateinit var accountRepositoryMock: AccountRepository
    private lateinit var protocolClientMock: ProtocolClient
    private lateinit var usersRepositoryMock: UsersRepository
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        accountRepositoryMock = PowerMockito.mock(AccountRepository::class.java)
        protocolClientMock = PowerMockito.mock(ProtocolClient::class.java)
        usersRepositoryMock = PowerMockito.mock(UsersRepository::class.java)

        // Configure mocks
        protocolClientMock.connectionStateChannel = ConflatedBroadcastChannel()
        protocolClientMock.errorsMessagesCallbacks = ArrayList()
        protocolClientMock.readCallbacks = ConcurrentLinkedDeque()
        usersRepositoryMock.loadedUsersIds = HashSet()
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))

        // Create mock ...
        authRepository = PowerMockito.spy(AuthRepository(protocolClientMock, accountRepositoryMock, Provider {
            return@Provider usersRepositoryMock
        }))

        authRepository.accountSessionStateChannel = Mockito.spy(authRepository.accountSessionStateChannel)
        authRepository.authSessionChannel = Mockito.spy(authRepository.authSessionChannel)

        // Change after listenConnectionState called
        Mockito.doAnswer {
            protocolClientMock.connectionStateChannel = PowerMockito.spy(protocolClientMock.connectionStateChannel)
            return@doAnswer Job()
        }.`when`(authRepository).listenConnectionState()
    }

    @After
    fun tearDown() {
        Mockito.reset(accountRepositoryMock)
        Mockito.reset(protocolClientMock)
        Mockito.reset(usersRepositoryMock)
        Mockito.reset(authRepository)
    }

    @Test
    fun testRequestCode_phone_validation() = runBlocking {
        try {
            authRepository
                    .requestCode("ABCDEF")
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestException) {
            assertEquals(Errors.INVALID_PARAMETERS, e.errorCode)
        }
    }

    @Test
    fun testRequestCode_network_exception() = runBlocking<Unit> {
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthCodeDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing ...
        authRepository.requestCode("79000000000").await()

        // Verifying ...
        Mockito.verify(authRepository.authSessionChannel, Mockito.never()).offer(any())
    }

    @Test
    fun testRequestCode_success() = runBlocking {
        val hash = randomBase64String(32)
        val status = Random.nextInt()

        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthCodeDTO>)
                    .coroutine!!
                    .resume(AuthCodeDTO().apply {
                        this.hash = hash
                        this.status = status
                    })
        }

        // Testing ...
        authRepository.requestCode("79000000000").await()

        // Verifying ...
        Mockito.verify(authRepository.authSessionChannel).offer(any())
        assertNotNull(authRepository.authSessionChannel.valueOrNull)
        assertEquals(hash, authRepository.authSessionChannel.value.hash)
        assertEquals(status, authRepository.authSessionChannel.value.status)
        assertEquals("79000000000", authRepository.authSessionChannel.value.phone)
    }

    @Test
    fun testRequestCode_error() = runBlocking {
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthCodeDTO>)
                    .coroutine!!
                    .resume(AuthCodeDTO().apply {
                        this.error = Errors.INVALID_PARAMETERS
                    })
        }

        try {
            // Testing ...
            authRepository
                    .requestCode("79000000000")
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestException) {
            // Verifying ...
            Mockito.verify(authRepository.authSessionChannel, Mockito.never()).offer(any())
            assertEquals(Errors.INVALID_PARAMETERS, e.errorCode)
        }
    }

    @Test
    fun testCheckCode_code_validation() = runBlocking {
        try {
            authRepository
                    .checkCode("79000000000", "ABCDF", randomBase64String(32))
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestException) {
            assertEquals(Errors.INVALID_PARAMETERS, e.errorCode)
        }
    }

    @Test
    fun testCheckCode_network_exception() = runBlocking<Unit> {
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthCheckCodeDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing ...
        val result = authRepository.checkCode("79000000000", "12345", randomBase64String(32)).await()

        // Verifying ...
        Mockito.verify(authRepository.authSessionChannel, Mockito.never()).offer(any())
        assertFalse(result)
    }

    @Test
    fun testCheckCode_code_expired_error() = runBlocking {
        val hash = randomBase64String(64)

        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthCheckCodeDTO>)
                    .coroutine!!
                    .resume(AuthCheckCodeDTO().apply {
                        this.error = Errors.CODE_EXPIRED
                    })
        }

        authRepository.authSessionChannel.offer(AuthSession(
                phone = "79000000000",
                code = "12345",
                hash = hash
        ))

        try {
            // Testing ...
            authRepository
                    .checkCode("79000000000", "12345", hash)
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestException) {
            // Verifying ...
            assertEquals(null, authRepository.authSessionChannel.valueOrNull)
            assertEquals(Errors.CODE_EXPIRED, e.errorCode)
        }
    }

    @Test
    fun testCheckCode_too_many_requests_error() = runBlocking {
        val hash = randomBase64String(64)

        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthCheckCodeDTO>)
                    .coroutine!!
                    .resume(AuthCheckCodeDTO().apply {
                        this.error = Errors.TOO_MANY_REQUESTS
                    })
        }

        authRepository.authSessionChannel.offer(AuthSession(
                phone = "79000000000",
                code = "12345",
                hash = hash
        ))

        try {
            // Testing ...
            authRepository
                    .checkCode("79000000000", "12345", hash)
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestException) {
            // Verifying ...
            assertEquals(null, authRepository.authSessionChannel.valueOrNull)
            assertEquals(Errors.TOO_MANY_REQUESTS, e.errorCode)
        }
    }

    @Test
    fun testCheckCode_other_errors() = runBlocking {
        val hash = randomBase64String(64)

        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthCheckCodeDTO>)
                    .coroutine!!
                    .resume(AuthCheckCodeDTO().apply {
                        this.error = Errors.INVALID_PARAMETERS
                    })
        }

        authRepository.authSessionChannel.offer(AuthSession(
                phone = "79000000000",
                code = "12345",
                hash = hash
        ))

        try {
            // Testing ...
            authRepository
                    .checkCode("79000000000", "12345", hash)
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestException) {
            // Verifying ...
            assertNotNull(authRepository.authSessionChannel.valueOrNull)
            assertEquals(Errors.INVALID_PARAMETERS, e.errorCode)
        }
    }

    @Test
    fun testCheckCode_success() = runBlocking {
        val hash = randomBase64String(64)

        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthCheckCodeDTO>)
                    .coroutine!!
                    .resume(AuthCheckCodeDTO())
        }

        authRepository.authSessionChannel.offer(AuthSession(
                phone = "79000000000",
                code = "12345",
                hash = hash
        ))

        // Testing ...
        val result = authRepository
                .checkCode("79000000000", "12345", hash)
                .await()

        // Verifying
        assertTrue(result)
    }

    @Test
    fun testSignIn_code_validation() = runBlocking {
        try {
            authRepository
                    .signIn("79000000000", "ABCDF", randomBase64String(32))
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestException) {
            assertEquals(Errors.INVALID_PARAMETERS, e.errorCode)
        }
    }

    @Test
    fun testSignIn_network_exception() = runBlocking {
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthSignInDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing ...
        val result = authRepository
                .signIn("79000000000", "12345", randomBase64String(32))
                .await()

        // Verifying ...
        Mockito.verify(authRepository.authSessionChannel, Mockito.never()).offer(any())
        assertFalse(result)
    }

    @Test
    fun testSignIn_code_expired_error() = runBlocking {
        val hash = randomBase64String(64)

        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthSignInDTO>)
                    .coroutine!!
                    .resume(AuthSignInDTO().apply {
                        this.error = Errors.CODE_EXPIRED
                    })
        }

        authRepository.authSessionChannel.offer(AuthSession(
                phone = "79000000000",
                code = "12345",
                hash = hash
        ))

        try {
            // Testing ...
            authRepository
                    .signIn("79000000000", "12345", hash)
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestException) {
            // Verifying ...
            assertEquals(null, authRepository.authSessionChannel.valueOrNull)
            assertEquals(Errors.CODE_EXPIRED, e.errorCode)
        }
    }

    @Test
    fun testSignIn_too_many_requests_error() = runBlocking {
        val hash = randomBase64String(64)

        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthSignInDTO>)
                    .coroutine!!
                    .resume(AuthSignInDTO().apply {
                        this.error = Errors.TOO_MANY_REQUESTS
                    })
        }

        authRepository.authSessionChannel.offer(AuthSession(
                phone = "79000000000",
                code = "12345",
                hash = hash
        ))

        try {
            // Testing ...
            authRepository
                    .signIn("79000000000", "12345", hash)
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestException) {
            // Verifying ...
            assertEquals(null, authRepository.authSessionChannel.valueOrNull)
            assertEquals(Errors.TOO_MANY_REQUESTS, e.errorCode)
        }
    }

    @Test
    fun testSignIn_success() = runBlocking {
        val hash = randomBase64String(64)
        val token = randomBase64String(64)
        val userDTO = UserDTO().apply {
            id = Random.nextLong()
            name = String(Random.nextBytes(12))
            nickname = String(Random.nextBytes(12))
            photo = String(Random.nextBytes(12))
        }

        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthSignInDTO>)
                    .coroutine!!
                    .resume(AuthSignInDTO().apply {
                        this.user = userDTO
                        this.token = token
                    })
        }

        authRepository.authSessionChannel.offer(AuthSession(
                phone = "79000000000",
                code = "12345",
                hash = hash
        ))

        // Testing ...
        val result = authRepository
                .signIn("79000000000", "12345", hash)
                .await()

        // Verifying
        Mockito.verify(authRepository).saveAccountSession(eq(token), any())
        assertTrue(usersRepositoryMock.loadedUsersIds.contains(userDTO.id))
        assertTrue(result)
    }

    @Test
    fun testSignUp_only_name_validation() = runBlocking {
        try {
            authRepository
                    .signUp("79000000000", "12345", randomBase64String(32), randomBase64String(64), "testaccount")
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestRegexException) {
            assertArrayEquals(intArrayOf(AuthRepository.AUTH_NAME_REGEX_ERROR), e.fields.toIntArray())
        }
    }

    @Test
    fun testSignUp_only_nickname_validation() = runBlocking {
        try {
            authRepository
                    .signUp("79000000000", "12345", randomBase64String(32), "Test name", randomBase64String(64))
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestRegexException) {
            assertArrayEquals(intArrayOf(AuthRepository.AUTH_NICKNAME_REGEX_ERROR), e.fields.toIntArray())
        }
    }

    @Test
    fun testSignUp_name_and_nickname_validation() = runBlocking {
        try {
            authRepository
                    .signUp("79000000000", "12345", randomBase64String(32), randomBase64String(64), randomBase64String(64))
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestRegexException) {
            assertArrayEquals(
                    intArrayOf(
                            AuthRepository.AUTH_NAME_REGEX_ERROR,
                            AuthRepository.AUTH_NICKNAME_REGEX_ERROR),
                    e.fields.toIntArray())
        }
    }

    @Test
    fun testSignUp_network_exception() = runBlocking {
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthSignUpDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing ...
        val result = authRepository
                .signUp("79000000000", "12345", randomBase64String(32), "Test account", "account")
                .await()

        // Verifying ...
        Mockito.verify(authRepository.authSessionChannel, Mockito.never()).offer(any())
        assertFalse(result)
    }

    @Test
    fun testSignUp_code_expired_error() = runBlocking {
        val hash = randomBase64String(64)

        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthSignUpDTO>)
                    .coroutine!!
                    .resume(AuthSignUpDTO().apply {
                        this.error = Errors.CODE_EXPIRED
                    })
        }

        authRepository.authSessionChannel.offer(AuthSession(
                phone = "79000000000",
                code = "12345",
                hash = hash
        ))

        try {
            // Testing ...
            authRepository
                    .signUp("79000000000", "12345", randomBase64String(32), "Test account", "account")
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestException) {
            // Verifying ...
            assertEquals(null, authRepository.authSessionChannel.valueOrNull)
            assertEquals(Errors.CODE_EXPIRED, e.errorCode)
        }
    }

    @Test
    fun testSignUp_too_many_requests_error() = runBlocking {
        val hash = randomBase64String(64)

        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthSignUpDTO>)
                    .coroutine!!
                    .resume(AuthSignUpDTO().apply {
                        this.error = Errors.TOO_MANY_REQUESTS
                    })
        }

        authRepository.authSessionChannel.offer(AuthSession(
                phone = "79000000000",
                code = "12345",
                hash = hash
        ))

        try {
            // Testing ...
            authRepository
                    .signUp("79000000000", "12345", randomBase64String(32), "Test account", "account")
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestException) {
            // Verifying ...
            assertEquals(null, authRepository.authSessionChannel.valueOrNull)
            assertEquals(Errors.TOO_MANY_REQUESTS, e.errorCode)
        }
    }

    @Test
    fun testSignUp_invalid_account_error() = runBlocking {
        val hash = randomBase64String(64)

        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthSignUpDTO>)
                    .coroutine!!
                    .resume(AuthSignUpDTO().apply {
                        this.error = Errors.INVALID_ACCOUNT
                    })
        }

        authRepository.authSessionChannel.offer(AuthSession(
                phone = "79000000000",
                code = "12345",
                hash = hash
        ))

        try {
            // Testing ...
            authRepository
                    .signUp("79000000000", "12345", randomBase64String(32), "Test account", "account")
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestException) {
            // Verifying ...
            assertEquals(null, authRepository.authSessionChannel.valueOrNull)
            assertEquals(Errors.INVALID_ACCOUNT, e.errorCode)
        }
    }

    @Test
    fun testSignUp_success() = runBlocking {
        val hash = randomBase64String(64)
        val token = randomBase64String(64)
        val userDTO = UserDTO().apply {
            id = Random.nextLong()
            name = String(Random.nextBytes(12))
            nickname = String(Random.nextBytes(12))
            photo = String(Random.nextBytes(12))
        }

        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthSignUpDTO>)
                    .coroutine!!
                    .resume(AuthSignUpDTO().apply {
                        this.user = userDTO
                        this.token = token
                    })
        }

        authRepository.authSessionChannel.offer(AuthSession(
                phone = "79000000000",
                code = "12345",
                hash = hash
        ))

        // Testing ...
        val result = authRepository
                .signUp("79000000000", "12345", randomBase64String(32), "Test account", "account")
                .await()

        // Verifying
        Mockito.verify(authRepository).saveAccountSession(eq(token), any())
        assertTrue(usersRepositoryMock.loadedUsersIds.contains(userDTO.id))
        assertTrue(result)
    }

    @Test
    fun testInstallAccountSession_network_exception() = runBlocking<Unit> {
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthSignUpDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing ...
        authRepository
                .installAccountSession(randomBase64String(32))
                .await()

        // Verifying ...
        Mockito.verify(authRepository.authSessionChannel, Mockito.never()).offer(any())
    }

    @Test
    fun testInstallAccountSession_invalid_account_error() = runBlocking {
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthImportDTO>)
                    .coroutine!!
                    .resume(AuthImportDTO().apply {
                        this.error = Errors.INVALID_ACCOUNT
                    })
        }

        // Testing ...
        authRepository
                .installAccountSession(randomBase64String(32))
                .await()

        // Verifying ...
        Mockito.verify(authRepository).removeAccountSession()
    }

    @Test
    fun testInstallAccountSession_other_errors() = runBlocking {
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthImportDTO>)
                    .coroutine!!
                    .resume(AuthImportDTO().apply {
                        this.error = Errors.INVALID_PARAMETERS
                    })
        }

        // Testing ...
        authRepository
                .installAccountSession(randomBase64String(32))
                .await()

        // Verifying ...
        Mockito.verify(authRepository, Mockito.never()).removeAccountSession()
    }

    @Test
    fun testInstallAccountSession_success() = runBlocking {
        val token = randomBase64String(64)
        val userDTO = UserDTO().apply {
            id = Random.nextLong()
            name = String(Random.nextBytes(12))
            nickname = String(Random.nextBytes(12))
            photo = String(Random.nextBytes(12))
        }

        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<AuthImportDTO>)
                    .coroutine!!
                    .resume(AuthImportDTO().apply {
                        this.user = userDTO
                        this.token = token
                    })
        }

        // Testing ...
        authRepository
                .installAccountSession(token)
                .await()

        // Verifying ...
        Mockito.verify(authRepository).saveAccountSession(eq(token), any())
    }

    @Test
    fun testSaveAccountSession() = runBlocking {
        val token = randomBase64String(32)
        val user = User(uid = Random.nextLong(),
                name = String(Random.nextBytes(12)),
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)))

        // Testing ...
        authRepository.saveAccountSession(token, user)

        // Verifying ...
        Mockito.verify(accountRepositoryMock).saveOrUpdateAccount(token, user)
        Mockito.verify(usersRepositoryMock).saveOrUpdateUsers(user)
        Mockito.verify(authRepository).notifyAccountSessionValid()
        assertTrue(usersRepositoryMock.loadedUsersIds.contains(user.uid))
    }

    @Test
    fun testRemoveAccountSession() = runBlocking {
        authRepository.removeAccountSession()

        // Verifying ...
        Mockito.verify(accountRepositoryMock).removeAccounts()
        Mockito.verify(authRepository).notifyAccountSessionInvalid()
    }

    @Test
    fun testNotifyAccountSessionValid() = runBlocking {
        authRepository.notifyAccountSessionValid()

        // Verifiyng ...
        Mockito.verify(authRepository.accountSessionStateChannel).offer(true)
        assertTrue(authRepository.sessionInstalled)
        assertNull(authRepository.authSessionChannel.valueOrNull)
        assertNull(authRepository.accountSessionStateChannel.valueOrNull)
    }

    @Test
    fun testNotifyAccountSessionInvalid() = runBlocking {
        authRepository.notifyAccountSessionInvalid()

        // Verifying ...
        Mockito.verify(authRepository.accountSessionStateChannel).offer(false)
        assertFalse(authRepository.sessionInstalled)
        assertNull(authRepository.authSessionChannel.valueOrNull)
        assertNull(authRepository.accountSessionStateChannel.valueOrNull)
    }

    @Test
    fun testCanExecuteNetworkRequest_not_session_but_connected_to_server() {
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        authRepository.sessionInstalled = false

        // Verifying
        assertFalse(authRepository.canExecuteNetworkRequest())
    }

    @Test
    fun testCanExecuteNetworkRequest_not_connected_to_server_with_session() {
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(false)
        authRepository.sessionInstalled = true

        // Verifying
        assertFalse(authRepository.canExecuteNetworkRequest())
    }

    @Test
    fun testCanExecuteNetworkRequest_not_session_and_not_connected_to_server() {
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(false)
        authRepository.sessionInstalled = false

        // Verifying
        assertFalse(authRepository.canExecuteNetworkRequest())
    }

    @Test
    fun testCanExecuteNetworkRequest_with_session_and_connection_with_server() {
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        authRepository.sessionInstalled = true

        // Verifying
        assertTrue(authRepository.canExecuteNetworkRequest())
    }

    @Test
    fun testMakeRequestWithSession_without_event_bus_notifying() = runBlocking<Unit> {
        protocolClientMock.connectionStateChannel = PowerMockito.spy(protocolClientMock.connectionStateChannel)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(false)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<CoreVersionDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        authRepository.sessionInstalled = false

        try {
            // Testing ...
            authRepository
                    .makeRequestWithSession<CoreVersionDTO>(protocolClientMock, "core.getVersion", notifyToEventBus = false)
                    .await()

            fail("NetworkException not throwed.")
        } catch (e: NetworkException) {
            Mockito.verify(protocolClientMock.connectionStateChannel, Mockito.never()).offer(ConnectionState.NO_CONNECTION)
        }
    }

    @Test
    fun testMakeRequestWithSession_with_event_bus_notifying() = runBlocking<Unit> {
        protocolClientMock.connectionStateChannel = PowerMockito.spy(protocolClientMock.connectionStateChannel)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(false)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<CoreVersionDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        authRepository.sessionInstalled = false

        try {
            // Testing ...
            authRepository
                    .makeRequestWithSession<CoreVersionDTO>(protocolClientMock, "core.getVersion", notifyToEventBus = true)
                    .await()

            fail("NetworkException not throwed.")
        } catch (e: NetworkException) {
            Mockito.verify(protocolClientMock.connectionStateChannel).offer(ConnectionState.NO_CONNECTION)
        }
    }

    @Test
    fun testMakeRequestWithSession() = runBlocking<Unit> {
        protocolClientMock.connectionStateChannel = PowerMockito.spy(protocolClientMock.connectionStateChannel)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<CoreVersionDTO>)
                    .coroutine!!
                    .resume(CoreVersionDTO().apply {
                        version = "0.5.1"
                    })
        }

        authRepository.sessionInstalled = true
        authRepository
                .makeRequestWithSession<CoreVersionDTO>(protocolClientMock, "core.getVersion", notifyToEventBus = true)
                .await()

        Mockito.verify(protocolClientMock.connectionStateChannel, Mockito.never()).offer(ConnectionState.NO_CONNECTION)
        Mockito.verify(protocolClientMock).sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())
    }
}