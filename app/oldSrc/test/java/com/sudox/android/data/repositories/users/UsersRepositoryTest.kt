package com.sudox.android.data.repositories.users

import com.sudox.android.data.database.dao.user.UserDao
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.models.users.dto.UserDTO
import com.sudox.android.data.models.users.dto.UsersInfoDTO
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.NetworkException
import com.sudox.protocol.models.ReadCallback
import com.sudox.common.helper.any
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.random.Random

@RunWith(PowerMockRunner::class)
@PrepareForTest(AuthRepository::class, AccountRepository::class, ProtocolClient::class, UserDao::class, UsersRepository::class)
class UsersRepositoryTest : Assert() {

    private lateinit var authRepositoryMock: AuthRepository
    private lateinit var accountRepositoryMock: AccountRepository
    private lateinit var protocolClientMock: ProtocolClient
    private lateinit var userDaoMock: UserDao
    private lateinit var usersRepositoryMock: UsersRepository

    @Before
    fun setUp() {
        authRepositoryMock = PowerMockito.mock(AuthRepository::class.java)
        accountRepositoryMock = PowerMockito.mock(AccountRepository::class.java)
        protocolClientMock = PowerMockito.mock(ProtocolClient::class.java)
        userDaoMock = PowerMockito.mock(UserDao::class.java)

        // Configure needed fields
        protocolClientMock.readCallbacks = ConcurrentLinkedDeque()
        authRepositoryMock.accountSessionStateChannel = ConflatedBroadcastChannel()

        // Create testable repository
        usersRepositoryMock = PowerMockito.spy(UsersRepository(
                authRepository = authRepositoryMock,
                accountRepository = accountRepositoryMock,
                protocolClient = protocolClientMock,
                userDao = userDaoMock))
    }

    @After
    fun tearDown() {
        Mockito.reset(authRepositoryMock)
        Mockito.reset(protocolClientMock)
        Mockito.reset(userDaoMock)
        Mockito.reset(accountRepositoryMock)
        Mockito.reset(usersRepositoryMock)
    }

    @Test
    fun testLoadUsers_empty_ids_list() = runBlocking {
        val result = usersRepositoryMock
                .loadUsers(listOf())
                .await()

        // Verifying
        assertNotNull(result)
        assertTrue(result.isEmpty())
    }

    @Test
    fun testLoadUsers_only_from_network() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L)

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            protocolClientMock
                    .readCallbacks
                    .first
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing
        usersRepositoryMock
                .loadUsers(ids, onlyFromNetwork = true)
                .await()

        // Verifying
        Mockito.verify(usersRepositoryMock).fetchUsers(ids, false)
        Mockito.verify(userDaoMock, Mockito.never()).loadByIds(any())
    }

    @Test
    fun testLoadUsers_only_from_network_without_connection() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L)

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(false)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            protocolClientMock
                    .readCallbacks
                    .first
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing
        val users = usersRepositoryMock
                .loadUsers(ids, onlyFromNetwork = true)
                .await()

        // Verifying
        Mockito.verify(usersRepositoryMock, Mockito.never()).fetchUsers(any(), ArgumentMatchers.anyBoolean())
        Mockito.verify(userDaoMock, Mockito.never()).loadByIds(any())
        assertTrue(users.isEmpty())
    }

    @Test
    fun testLoadUsers_only_from_network_but_connection_dropped_during_request() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L)

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            protocolClientMock
                    .readCallbacks
                    .first
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing
        val users = usersRepositoryMock
                .loadUsers(ids, onlyFromNetwork = true)
                .await()

        Mockito.verify(usersRepositoryMock).fetchUsers(ids, false)
        Mockito.verify(userDaoMock, Mockito.never()).loadByIds(any())
        assertTrue(users.isEmpty())
    }

    @Test
    fun testLoadUsers_only_from_database() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L)

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            protocolClientMock
                    .readCallbacks
                    .first
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing
        usersRepositoryMock
                .loadUsers(ids, onlyFromDatabase = true)
                .await()

        // Verifying
        Mockito.verify(usersRepositoryMock, Mockito.never()).fetchUsers(any(), ArgumentMatchers.anyBoolean())
        Mockito.verify(userDaoMock).loadByIds(ids)
    }

    @Test
    fun testLoadUsers_only_from_database_without_internet() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L)

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(false)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            protocolClientMock
                    .readCallbacks
                    .first
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing
        usersRepositoryMock
                .loadUsers(ids, onlyFromDatabase = true)
                .await()

        // Verifying
        Mockito.verify(usersRepositoryMock, Mockito.never()).fetchUsers(any(), ArgumentMatchers.anyBoolean())
        Mockito.verify(userDaoMock).loadByIds(ids)
    }

    @Test
    fun testLoadUsers_typical_without_internet() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L)

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(false)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            protocolClientMock
                    .readCallbacks
                    .first
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing
        usersRepositoryMock
                .loadUsers(ids)
                .await()

        // Verifying
        Mockito.verify(usersRepositoryMock, Mockito.never()).fetchUsers(any(), ArgumentMatchers.anyBoolean())
        Mockito.verify(userDaoMock).loadByIds(ids)
    }

    @Test
    fun testLoadUsers_typical_not_loaded() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L)

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .first as ReadCallback<UsersInfoDTO>)
                    .coroutine!!
                    .resume(UsersInfoDTO().apply {
                        users = ArrayList()

                        repeat(3) {
                            users!!.plusAssign(UserDTO().apply {
                                id = ids[it]
                                name = String(Random.nextBytes(12))
                                nickname = String(Random.nextBytes(12))
                                photo = String(Random.nextBytes(12))
                            })
                        }
                    })
        }

        // Testing
        val users = usersRepositoryMock
                .loadUsers(ids)
                .await()

        // Verifying
        Mockito.verify(userDaoMock, Mockito.never()).loadByIds(any())
        Mockito.verify(usersRepositoryMock).fetchUsers(ids)
        assertEquals(ids.size, users.size)
        assertEquals(ids.size, usersRepositoryMock.loadedUsersIds.size)
    }

    @Test
    fun testLoadUsers_cached_and_internet() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L)
        val usersDTOs = ArrayList<UserDTO>().apply {
            repeat(3) {
                plusAssign(UserDTO().apply {
                    id = ids[it]
                    name = String(Random.nextBytes(12))
                    nickname = String(Random.nextBytes(12))
                    photo = String(Random.nextBytes(12))
                })
            }
        }

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<UsersInfoDTO>)
                    .coroutine!!
                    .resume(UsersInfoDTO().apply { this.users = usersDTOs })
        }

        // Emulate first loading ...
        val fetchedUsers = usersRepositoryMock.loadUsers(ids).await()

        // Start loading ...
        Mockito.reset(usersRepositoryMock)
        Mockito.reset(userDaoMock)
        Mockito.`when`(userDaoMock.loadByIds(ids))
                .thenReturn(fetchedUsers)

        val cachedUsers = usersRepositoryMock.loadUsers(ids).await()

        // Verifying
        Mockito.verify(userDaoMock).loadByIds(ids)
        Mockito.verify(usersRepositoryMock, Mockito.never()).fetchUsers(any(), ArgumentMatchers.anyBoolean())
        assertEquals(fetchedUsers.size, cachedUsers.size)
    }

    @Test
    fun testLoadUsers_cached_and_without_internet() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L)
        val usersDTOs = ArrayList<UserDTO>().apply {
            repeat(3) {
                plusAssign(UserDTO().apply {
                    id = ids[it]
                    name = String(Random.nextBytes(12))
                    nickname = String(Random.nextBytes(12))
                    photo = String(Random.nextBytes(12))
                })
            }
        }

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<UsersInfoDTO>)
                    .coroutine!!
                    .resume(UsersInfoDTO().apply { this.users = usersDTOs })
        }

        // Emulate first loading ...
        val fetchedUsers = usersRepositoryMock.loadUsers(ids).await()

        // Start loading ...
        Mockito.reset(authRepositoryMock)
        Mockito.reset(protocolClientMock)
        Mockito.reset(usersRepositoryMock)
        Mockito.reset(userDaoMock)
        Mockito.`when`(userDaoMock.loadByIds(ids)).thenReturn(fetchedUsers)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(false)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            protocolClientMock
                    .readCallbacks
                    .first
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        val cachedUsers = usersRepositoryMock.loadUsers(ids).await()

        // Verifying
        Mockito.verify(userDaoMock).loadByIds(ids)
        Mockito.verify(usersRepositoryMock, Mockito.never()).fetchUsers(any(), ArgumentMatchers.anyBoolean())
        assertEquals(fetchedUsers.size, cachedUsers.size)
    }

    @Test
    fun testLoadUsers_standard_partially_fetching() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L, 4L)
        val fetchIds = listOf(1L, 2L)
        val storedIds = listOf(3L, 4L)
        val storedUsers = ArrayList<UserDTO>().apply {
            repeat(2) {
                plusAssign(UserDTO().apply {
                    id = storedIds[it]
                    name = String(Random.nextBytes(12))
                    nickname = String(Random.nextBytes(12))
                    photo = String(Random.nextBytes(12))
                })
            }
        }

        val fetchUsers = ArrayList<UserDTO>().apply {
            repeat(2) {
                plusAssign(UserDTO().apply {
                    id = fetchIds[it]
                    name = String(Random.nextBytes(12))
                    nickname = String(Random.nextBytes(12))
                    photo = String(Random.nextBytes(12))
                })
            }
        }

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<UsersInfoDTO>)
                    .coroutine!!
                    .resume(UsersInfoDTO().apply { this.users = storedUsers })
        }

        val stored = usersRepositoryMock.loadUsers(storedIds).await() // Load ids to storage ...

        // Clean
        Mockito.reset(usersRepositoryMock)
        Mockito.reset(userDaoMock)
        Mockito.reset(protocolClientMock)
        Mockito.`when`(userDaoMock.loadByIds(storedIds)).thenReturn(stored)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<UsersInfoDTO>)
                    .coroutine!!
                    .resume(UsersInfoDTO().apply { this.users = fetchUsers })
        }

        val users = usersRepositoryMock.loadUsers(ids).await() // Load ids for fetching ...

        // Verifying
        Mockito.verify(userDaoMock).loadByIds(storedIds)
        Mockito.verify(usersRepositoryMock).fetchUsers(fetchIds)
        assertEquals(users.size, ids.size)
    }

    @Test
    fun testLoadUsers_standard_partially_loaded_but_connection_dropped_was_during_request() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L, 4L)
        val fetchIds = listOf(1L, 2L)
        val storedIds = listOf(3L, 4L)
        val storedUsers = ArrayList<UserDTO>().apply {
            repeat(2) {
                plusAssign(UserDTO().apply {
                    id = storedIds[it]
                    name = String(Random.nextBytes(12))
                    nickname = String(Random.nextBytes(12))
                    photo = String(Random.nextBytes(12))
                })
            }
        }

        val fetchUsers = ArrayList<UserDTO>().apply {
            repeat(2) {
                plusAssign(UserDTO().apply {
                    id = fetchIds[it]
                    name = String(Random.nextBytes(12))
                    nickname = String(Random.nextBytes(12))
                    photo = String(Random.nextBytes(12))
                })
            }
        }

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<UsersInfoDTO>)
                    .coroutine!!
                    .resume(UsersInfoDTO().apply { this.users = storedUsers })
        }

        val stored = usersRepositoryMock.loadUsers(storedIds).await() // Load ids to storage ..

        Mockito.reset(usersRepositoryMock)
        Mockito.reset(authRepositoryMock)
        Mockito.reset(protocolClientMock)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(userDaoMock.loadByIds(storedIds)).thenReturn(stored)
        Mockito.`when`(userDaoMock.loadByIds(fetchIds)).thenReturn(fetchUsers as List<User>)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            protocolClientMock
                    .readCallbacks
                    .removeFirst()
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        val users = usersRepositoryMock.loadUsers(ids).await() // Load ids for fetching ...

        // Verifying
        Mockito.verify(userDaoMock).loadByIds(storedIds)
        Mockito.verify(userDaoMock).loadByIds(fetchIds)
        Mockito.verify(usersRepositoryMock).fetchUsers(fetchIds)
        assertEquals(ids.size, users.size)
    }

    @Test
    fun testFetchUsers_without_internet_load_from_database() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L)
        val users = ArrayList<User>().apply {
            repeat(ids.size) {
                User(uid = ids[it],
                        name = String(Random.nextBytes(12)),
                        nickname = String(Random.nextBytes(12)),
                        photo = String(Random.nextBytes(12)))
            }
        }

        // Testing ...
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(userDaoMock.loadByIds(ids)).thenReturn(users)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            protocolClientMock
                    .readCallbacks
                    .first
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        val fetchedUsers = usersRepositoryMock.fetchUsers(ids, loadFromDatabaseIfConnectionDropped = true)

        // Verifying ...
        Mockito.verify(userDaoMock).loadByIds(ids)
        assertArrayEquals(users.toTypedArray(), fetchedUsers.toTypedArray())
    }

    @Test
    fun testFetchUsers_without_internet_not_load_from_database() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L)
        val users = ArrayList<User>().apply {
            repeat(ids.size) {
                User(uid = ids[it],
                        name = String(Random.nextBytes(12)),
                        nickname = String(Random.nextBytes(12)),
                        photo = String(Random.nextBytes(12)))
            }
        }

        // Testing ...
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(userDaoMock.loadByIds(ids)).thenReturn(users)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            protocolClientMock
                    .readCallbacks
                    .first
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        val fetchedUsers = usersRepositoryMock.fetchUsers(ids, loadFromDatabaseIfConnectionDropped = false)

        // Verifying ...
        Mockito.verify(userDaoMock, Mockito.never()).loadByIds(ids)
        assertTrue(fetchedUsers.isEmpty())
    }

    @Test
    fun testFetchUsers_success() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L, 4L)
        val usersDTOs = ArrayList<UserDTO>().apply {
            repeat(ids.size) {
                plusAssign(UserDTO().apply {
                    id = ids[it]
                    name = String(Random.nextBytes(12))
                    nickname = String(Random.nextBytes(12))
                    photo = String(Random.nextBytes(12))
                })
            }
        }

        // Testing ...
        Mockito.`when`(userDaoMock.filterContactsIds(ids)).thenReturn(ArrayList())
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .first as ReadCallback<UsersInfoDTO>)
                    .coroutine!!
                    .resume(UsersInfoDTO().apply { this.users = usersDTOs })
        }

        val loadedUsers = usersRepositoryMock.fetchUsers(ids)

        // Verifying
        Mockito.verify(usersRepositoryMock).saveOrUpdateUsers(any())
        assertEquals(ids.size, loadedUsers.size)
    }

    @Test
    fun testFetchUsers_success_but_exists_loaded_contacts() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L, 4L)
        val loadedContactsIds = arrayListOf(3L, 4L)
        val usersDTOs = ArrayList<UserDTO>().apply {
            repeat(ids.size) {
                plusAssign(UserDTO().apply {
                    id = ids[it]
                    name = String(Random.nextBytes(12))
                    nickname = String(Random.nextBytes(12))
                    photo = String(Random.nextBytes(12))
                })
            }
        }

        // Testing ...
        Mockito.`when`(userDaoMock.filterContactsIds(ids)).thenReturn(loadedContactsIds)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .first as ReadCallback<UsersInfoDTO>)
                    .coroutine!!
                    .resume(UsersInfoDTO().apply { this.users = usersDTOs })
        }

        val loadedUsers = usersRepositoryMock.fetchUsers(ids)

        // Verifying ...
        Mockito.verify(usersRepositoryMock).saveOrUpdateUsers(any())
        assertEquals(loadedUsers.size, ids.size)
        assertArrayEquals(loadedContactsIds.toArray(), loadedUsers.filter { it.isContact }.map { it.uid }.toTypedArray())
    }

    @Test
    fun testFetchUsers_success_but_not_all_requested_users_exists() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L, 4L)
        val existsIds = arrayListOf(1L, 2L)
        val notExistsIds = arrayListOf(3L, 4L)
        val usersDTOs = ArrayList<UserDTO>().apply {
            repeat(2) {
                plusAssign(UserDTO().apply {
                    id = ids[it]
                    name = String(Random.nextBytes(12))
                    nickname = String(Random.nextBytes(12))
                    photo = String(Random.nextBytes(12))
                })
            }
        }

        // Testing ...
        Mockito.`when`(userDaoMock.filterContactsIds(ids)).thenReturn(ArrayList())
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .first as ReadCallback<UsersInfoDTO>)
                    .coroutine!!
                    .resume(UsersInfoDTO().apply { this.users = usersDTOs })
        }

        val loadedUsers = usersRepositoryMock.fetchUsers(ids)

        // Verifying
        Mockito.verify(usersRepositoryMock).saveOrUpdateUsers(any())
        Mockito.verify(usersRepositoryMock).removeUsers(*notExistsIds.toLongArray(), updateExists = false)
        assertArrayEquals(existsIds.toArray(), loadedUsers.map { it.uid }.toTypedArray())
    }

    @Test
    fun testFetchUsers_error_all_users_invalid() = runBlocking<Unit> {
        val ids = listOf(1L, 2L, 3L, 4L)

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .first as ReadCallback<UsersInfoDTO>)
                    .coroutine!!
                    .resume(UsersInfoDTO().apply { error = Errors.INVALID_USERS })
        }

        val loadedUsers = usersRepositoryMock.fetchUsers(ids)

        // Verifying
        Mockito.verify(usersRepositoryMock).removeUsers(*ids.toLongArray(), updateExists = false)
        Mockito.verify(usersRepositoryMock, never()).saveOrUpdateUsers(any())
        assertTrue(loadedUsers.isEmpty())
    }

    @Test
    fun testUsersUpdates_single_user_and_single_callback() = runBlocking<Unit> {
        var times = 0
        val user = User(1L, "Test", "Test", "col.#fff.#fff")
        val updatedUser = User(1L, "Test", "Test", "photo.145879")
        val updateCallback = object : (User) -> (Unit) { override fun invoke(p1: User) { times++ } }

        // Testing ...
        usersRepositoryMock.subscribeToUserUpdates(1L, updateCallback)
        usersRepositoryMock.notifyUsersUpdated(user)
        usersRepositoryMock.notifyUsersUpdated(updatedUser)

        // Verifying
        assertEquals(2, times)

        // Testing unsubscribe ...
        times = 0
        usersRepositoryMock.unsubscribeFromUserUpdates(updateCallback)
        usersRepositoryMock.notifyUsersUpdated(user)
        usersRepositoryMock.notifyUsersUpdated(updatedUser)

        // Verifying
        assertEquals(0, times)
    }

    @Test
    fun testUsersUpdates_single_user_and_many_callbacks() {
        var times = 0
        val user = User(1L, "Test", "Test", "col.#fff.#fff")
        val updatedUser = User(1L, "FFF", "FFF", "photo.145879")
        val updateCallbackFirst = object : (User) -> (Unit) { override fun invoke(p1: User) { times++ } }
        val updateCallbackSecond = object : (User) -> (Unit) { override fun invoke(p1: User) { times++ } }

        // Testing ...
        usersRepositoryMock.subscribeToUserUpdates(1L, updateCallbackFirst)
        usersRepositoryMock.subscribeToUserUpdates(1L, updateCallbackSecond)
        usersRepositoryMock.notifyUsersUpdated(user)
        usersRepositoryMock.notifyUsersUpdated(updatedUser)

        // Verifying
        assertEquals(4, times)

        // Testing unsubscribe ...
        times = 0
        usersRepositoryMock.unsubscribeFromUsersUpdates(1L)
        usersRepositoryMock.notifyUsersUpdated(user)
        usersRepositoryMock.notifyUsersUpdated(updatedUser)

        // Verifying
        assertEquals(0, times)
    }

    @Test
    fun testUsersUpdates_many_users_and_single_callback() {
        var times = 0
        val userFirst = User(1L, "Test", "Test", "col.#fff.#fff")
        val userSecond = User(2L, "Tt", "st", "photo.145879")
        val updatedUserFirst = User(1L, "Tst", "est", "photo.145879")
        val updatedUserSecond = User(2L, "Tt", "Tt", "photo.145879")
        val updateCallback = object : (User) -> (Unit) { override fun invoke(p1: User) { times++ } }

        // Testing ...
        usersRepositoryMock.subscribeToUserUpdates(1L, updateCallback)
        usersRepositoryMock.notifyUsersUpdated(userFirst)
        usersRepositoryMock.notifyUsersUpdated(userSecond)
        usersRepositoryMock.notifyUsersUpdated(updatedUserFirst)
        usersRepositoryMock.notifyUsersUpdated(updatedUserSecond)

        // Verifying
        assertEquals(2, times)

        // Testing unsubscribe ...
        times = 0
        usersRepositoryMock.unsubscribeFromUserUpdates(updateCallback)
        usersRepositoryMock.notifyUsersUpdated(userFirst)
        usersRepositoryMock.notifyUsersUpdated(userSecond)
        usersRepositoryMock.notifyUsersUpdated(updatedUserFirst)
        usersRepositoryMock.notifyUsersUpdated(updatedUserSecond)

        // Verifying
        assertEquals(0, times)
    }

    @Test
    fun testUsersUpdates_many_users_and_single_callbacks() {
        var firstTimes = 0
        var secondTimes = 0
        val userFirst = User(1L, "Test", "Test", "col.#fff.#fff")
        val userSecond = User(2L, "Tt", "st", "photo.145879")
        val updatedUserFirst = User(1L, "Tst", "est", "photo.145879")
        val updatedUserSecond = User(2L, "Tt", "Tt", "photo.145879")
        val updateCallbackFirst = object : (User) -> (Unit) { override fun invoke(p1: User) { firstTimes++ } }
        val updateCallbackSecond = object : (User) -> (Unit) { override fun invoke(p1: User) { secondTimes++ } }

        // Testing ...
        usersRepositoryMock.subscribeToUserUpdates(1L, updateCallbackFirst)
        usersRepositoryMock.subscribeToUserUpdates(2L, updateCallbackSecond)
        usersRepositoryMock.notifyUsersUpdated(userFirst)
        usersRepositoryMock.notifyUsersUpdated(userSecond)
        usersRepositoryMock.notifyUsersUpdated(updatedUserFirst)
        usersRepositoryMock.notifyUsersUpdated(updatedUserSecond)

        // Verifying
        assertEquals(2, firstTimes)
        assertEquals(2, secondTimes)

        // Testing unsubscribe ...
        firstTimes = 0
        secondTimes = 0
        usersRepositoryMock.unsubscribeFromUserUpdates(updateCallbackFirst)
        usersRepositoryMock.unsubscribeFromUserUpdates(updateCallbackSecond)
        usersRepositoryMock.notifyUsersUpdated(userFirst)
        usersRepositoryMock.notifyUsersUpdated(userSecond)
        usersRepositoryMock.notifyUsersUpdated(updatedUserFirst)
        usersRepositoryMock.notifyUsersUpdated(updatedUserSecond)

        // Verifying
        assertEquals(0, firstTimes)
        assertEquals(0, secondTimes)
    }

    @Test
    fun testUsersSaving() {
        val user = User(1L, "Test", "Test", "col.#fff.#fff")

        // Testing ...

    }
}