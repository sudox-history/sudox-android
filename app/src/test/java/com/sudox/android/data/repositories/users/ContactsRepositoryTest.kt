package com.sudox.android.data.repositories.users

import android.accounts.Account
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import com.sudox.android.common.helpers.clear
import com.sudox.android.common.helpers.loadContactsFromPhone
import com.sudox.android.data.database.dao.user.UserDao
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.exceptions.InternalRequestException
import com.sudox.android.data.exceptions.RequestException
import com.sudox.android.data.exceptions.RequestRegexException
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.models.common.InternalErrors
import com.sudox.android.data.models.contacts.dto.*
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.helpers.randomBase64String
import com.sudox.protocol.models.NetworkException
import com.sudox.protocol.models.ReadCallback
import com.sudox.protocol.models.enums.ConnectionState
import com.sudox.tests.helpers.any
import com.sudox.tests.helpers.eq
import kotlinx.coroutines.CompletableDeferred
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
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

@RunWith(PowerMockRunner::class)
@PrepareForTest(Cursor::class, ContentResolver::class, Context::class, ProtocolClient::class, AuthRepository::class, AccountRepository::class, UsersRepository::class, UserDao::class, ContactsRepository::class, fullyQualifiedNames = ["com.sudox.android.common.helpers.AndroidHelperKt"])
class ContactsRepositoryTest : Assert() {

    private lateinit var protocolClientMock: ProtocolClient
    private lateinit var authRepositoryMock: AuthRepository
    private lateinit var accountRepositoryMock: AccountRepository
    private lateinit var usersRepositoryMock: UsersRepository
    private lateinit var userDaoMock: UserDao
    private lateinit var contextMock: Context
    private lateinit var contactsRepository: ContactsRepository

    @Before
    fun setUp() {
        protocolClientMock = PowerMockito.mock(ProtocolClient::class.java)
        authRepositoryMock = PowerMockito.mock(AuthRepository::class.java)
        accountRepositoryMock = PowerMockito.mock(AccountRepository::class.java)
        usersRepositoryMock = PowerMockito.mock(UsersRepository::class.java)
        userDaoMock = PowerMockito.mock(UserDao::class.java)
        contextMock = Mockito.mock(Context::class.java)

        // Configure needed mocks
        protocolClientMock.readCallbacks = ConcurrentLinkedDeque()
        protocolClientMock.connectionStateChannel = ConflatedBroadcastChannel()
        authRepositoryMock.accountSessionStateChannel = ConflatedBroadcastChannel()
        usersRepositoryMock.loadedUsersIds = HashSet()

        // Mock context
        PowerMockito.mockStatic(Class.forName("com.sudox.android.common.helpers.AndroidHelperKt"))

        // Create testable repository
        contactsRepository = PowerMockito.spy(ContactsRepository(
                protocolClient = protocolClientMock,
                authRepository = authRepositoryMock,
                accountRepository = accountRepositoryMock,
                usersRepository = usersRepositoryMock,
                userDao = userDaoMock,
                context = contextMock))
    }

    @After
    fun tearDown() {
        Mockito.reset(protocolClientMock)
        Mockito.reset(authRepositoryMock)
        Mockito.reset(accountRepositoryMock)
        Mockito.reset(usersRepositoryMock)
        Mockito.reset(userDaoMock)
        Mockito.reset(contextMock)
        Mockito.reset(contactsRepository)
    }

    @Test
    fun testSyncNotifyContacts() = runBlocking<Unit> {
        val ids = arrayListOf(1L, 2L, 3L)
        val users = ArrayList<User>()
        val dto = ContactsIdsListDTO().apply { this.ids = ids }

        repeat(ids.size) {
            users.plusAssign(User(uid = ids[it],
                    name = String(Random.nextBytes(12)),
                    nickname = String(Random.nextBytes(12)),
                    photo = String(Random.nextBytes(12))))
        }

        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadActualUsers(ids)).thenReturn(CompletableDeferred(users))
        Mockito.`when`(userDaoMock.loadContactsIds()).thenReturn(LongArray(0))

        // Testing ...
        contactsRepository
                .syncNotifyContacts(dto)
                .await()

        // Verifying ...
        Mockito.verify(contactsRepository).fetchNewContacts(ids)
        assertArrayEquals(users.toTypedArray(), contactsRepository.contactsChannel.value.toTypedArray())
    }

    @Test
    fun testSyncNotifyContacts_users_not_loaded() = runBlocking<Unit> {
        val ids = arrayListOf(1L, 2L, 3L)
        val users = ArrayList<User>()
        val dto = ContactsIdsListDTO().apply { this.ids = ids }

        repeat(ids.size) {
            users.plusAssign(User(uid = ids[it],
                    name = String(Random.nextBytes(12)),
                    nickname = String(Random.nextBytes(12)),
                    photo = String(Random.nextBytes(12))))
        }

        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadActualUsers(ids)).thenReturn(CompletableDeferred(ArrayList()))
        Mockito.`when`(userDaoMock.loadContactsIds()).thenReturn(LongArray(0))

        // Testing ...
        contactsRepository
                .syncNotifyContacts(dto)
                .await()

        // Verifying ...
        Mockito.verify(contactsRepository).fetchNewContacts(ids)
        assertNull(contactsRepository.contactsChannel.valueOrNull)
    }

    @Test
    fun testEditNotifyContact_when_user_null() = runBlocking {
        val contactEditDTO = ContactEditDTO().apply { id = Random.nextLong() }
        val deferred = CompletableDeferred<User?>().apply {
            complete(null)
        }

        // Testing ...
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadActualUser(ArgumentMatchers.anyLong())).thenReturn(deferred)

        contactsRepository.editNotifyContact(contactEditDTO).await()

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUser(contactEditDTO.id)
        Mockito.verify(contactsRepository, Mockito.never()).addContactMark(any())
        Mockito.verify(contactsRepository, Mockito.never()).notifyContactUpdated(any())
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
    }

    @Test
    fun testEditNotifyContact() = runBlocking {
        val contactEditDTO = ContactEditDTO().apply { id = Random.nextLong() }
        val user = User(uid = contactEditDTO.id,
                name = randomBase64String(32),
                nickname = randomBase64String(32),
                photo = "")

        // Testing ...
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadActualUser(ArgumentMatchers.anyLong())).thenReturn(CompletableDeferred(user))

        contactsRepository.editNotifyContact(contactEditDTO).await()

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUser(contactEditDTO.id)
        Mockito.verify(contactsRepository).addContactMark(user)
        Mockito.verify(contactsRepository).notifyContactUpdated(user)
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
    }

    @Test
    fun testSaveNotifyContact_when_user_null() = runBlocking {
        val contactAddDTO = ContactAddDTO().apply { id = Random.nextLong() }
        val deferred = CompletableDeferred<User?>().apply {
            complete(null)
        }

        // Testing ...
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadActualUser(ArgumentMatchers.anyLong())).thenReturn(deferred)

        contactsRepository.saveNotifyContact(contactAddDTO).await()

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUser(contactAddDTO.id)
        Mockito.verify(contactsRepository, Mockito.never()).addContactMark(any())
        Mockito.verify(contactsRepository, Mockito.never()).notifyContactAdded(any())
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
    }

    @Test
    fun testSaveNotifyContact() = runBlocking {
        val contactAddDTO = ContactAddDTO().apply { id = Random.nextLong() }
        val user = User(uid = contactAddDTO.id,
                name = randomBase64String(32),
                nickname = randomBase64String(32),
                photo = "")

        // Testing ...
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadActualUser(ArgumentMatchers.anyLong())).thenReturn(CompletableDeferred(user))

        contactsRepository.saveNotifyContact(contactAddDTO).await()

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUser(contactAddDTO.id)
        Mockito.verify(contactsRepository).addContactMark(user)
        Mockito.verify(contactsRepository).notifyContactAdded(user)
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
    }

    @Test
    fun testRemoveNotifyContact_when_user_null() = runBlocking {
        val contactRemoveDTO = ContactRemoveDTO().apply { id = Random.nextLong() }
        val deferred = CompletableDeferred<User?>().apply {
            complete(null)
        }

        // Testing ...
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadActualUser(ArgumentMatchers.anyLong())).thenReturn(deferred)

        contactsRepository.removeNotifyContact(contactRemoveDTO).await()

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUser(contactRemoveDTO.id)
        Mockito.verify(contactsRepository, Mockito.never()).removeContactMark(any())
        Mockito.verify(contactsRepository, Mockito.never()).notifyContactRemoved(contactRemoveDTO.id)
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
    }

    @Test
    fun testRemoveNotifyContact() = runBlocking {
        val contactRemoveDTO = ContactRemoveDTO().apply { id = Random.nextLong() }
        val user = User(uid = contactRemoveDTO.id,
                name = randomBase64String(32),
                nickname = randomBase64String(32),
                photo = "")

        // Testing ...
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadActualUser(ArgumentMatchers.anyLong())).thenReturn(CompletableDeferred(user))

        contactsRepository.removeNotifyContact(contactRemoveDTO).await()

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUser(contactRemoveDTO.id)
        Mockito.verify(contactsRepository).removeContactMark(user)
        Mockito.verify(contactsRepository).notifyContactRemoved(user.uid)
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
    }

    @Test
    fun testLoadContacts_with_internet() = runBlocking<Unit> {
        val ids = arrayListOf(1L, 2L, 3L)
        val users = ArrayList<User>()

        repeat(ids.size) {
            users.plusAssign(User(uid = ids[it],
                    name = String(Random.nextBytes(12)),
                    nickname = String(Random.nextBytes(12)),
                    photo = String(Random.nextBytes(12))))
        }

        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(userDaoMock.loadContactsIds()).thenReturn(LongArray(0))
        Mockito.`when`(userDaoMock.loadContacts()).thenReturn(users)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.loadActualUsers(ids)).thenReturn(CompletableDeferred(users))
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactsIdsListDTO>)
                    .coroutine!!
                    .resume(ContactsIdsListDTO().apply { this.ids = ids })
        }

        // Testing ...
        contactsRepository.loadContacts().await()

        // Verifying ...
        Mockito.verify(contactsRepository).fetchContacts()
        Mockito.verify(userDaoMock, Mockito.times(1)).loadContacts()
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
        assertArrayEquals(users.toTypedArray(), contactsRepository.contactsChannel.value.toTypedArray())
    }

    @Test
    fun testLoadContacts_without_internet_and_cached_data() = runBlocking<Unit> {
        val ids = arrayListOf(1L, 2L, 3L)
        val users = ArrayList<User>()

        repeat(ids.size) {
            users.plusAssign(User(uid = ids[it],
                    name = String(Random.nextBytes(12)),
                    nickname = String(Random.nextBytes(12)),
                    photo = String(Random.nextBytes(12))))
        }

        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(userDaoMock.loadContactsIds()).thenReturn(ids.toLongArray())
        Mockito.`when`(userDaoMock.loadContacts()).thenReturn(users)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(false)
        Mockito.`when`(usersRepositoryMock.loadUsers(ids)).thenReturn(CompletableDeferred(users))
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(false)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactsIdsListDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing ...
        contactsRepository.contactsChannel.clear()
        contactsRepository.loadContacts().await()

        // Verifying ...
        Mockito.verify(contactsRepository, Mockito.never()).fetchContacts()
        Mockito.verify(userDaoMock).loadContacts()
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
        assertArrayEquals(users.toTypedArray(), contactsRepository.contactsChannel.value.toTypedArray())
    }

    @Test
    fun testLoadContacts_without_internet_but_with_cached_data() = runBlocking<Unit> {
        val ids = arrayListOf(1L, 2L, 3L)
        val users = ArrayList<User>()
        val loaded = ArrayList<User>()

        repeat(ids.size) {
            users.plusAssign(User(uid = ids[it],
                    name = String(Random.nextBytes(12)),
                    nickname = String(Random.nextBytes(12)),
                    photo = String(Random.nextBytes(12))))
        }

        repeat(loaded.size) {
            users.plusAssign(User(uid = ids[it],
                    name = String(Random.nextBytes(12)),
                    nickname = String(Random.nextBytes(12)),
                    photo = String(Random.nextBytes(12))))
        }

        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(userDaoMock.loadContactsIds()).thenReturn(ids.toLongArray())
        Mockito.`when`(userDaoMock.loadContacts()).thenReturn(users)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(false)
        Mockito.`when`(usersRepositoryMock.loadUsers(ids)).thenReturn(CompletableDeferred(users))
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(false)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactsIdsListDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing ...
        contactsRepository.contactsChannel.offer(loaded)
        contactsRepository.loadContacts().await()

        // Verifying ...
        Mockito.verify(contactsRepository, Mockito.never()).fetchContacts()
        Mockito.verify(userDaoMock, Mockito.never()).loadContacts()
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
        assertArrayEquals(loaded.toTypedArray(), contactsRepository.contactsChannel.value.toTypedArray())
    }

    @Test
    fun testLoadContacts_users_not_loaded() = runBlocking {
        val ids = arrayListOf(1L, 2L, 3L)
        val users = ArrayList<User>()

        repeat(ids.size) {
            users.plusAssign(User(uid = ids[it],
                    name = String(Random.nextBytes(12)),
                    nickname = String(Random.nextBytes(12)),
                    photo = String(Random.nextBytes(12))))
        }

        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(userDaoMock.loadContactsIds()).thenReturn(LongArray(0))
        Mockito.`when`(userDaoMock.loadContacts()).thenReturn(users)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.loadActualUsers(ids)).thenReturn(CompletableDeferred(ArrayList()))
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactsIdsListDTO>)
                    .coroutine!!
                    .resume(ContactsIdsListDTO().apply { this.ids = ids })
        }

        // Testing ...
        contactsRepository.loadContacts().await()

        // Verifying ...
        Mockito.verify(contactsRepository).fetchContacts()
        Mockito.verify(userDaoMock, Mockito.times(1)).loadContacts()
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
        assertNull(contactsRepository.contactsChannel.valueOrNull)
    }

    @Test
    fun testFetchContacts_error_no_contacts() {
        val ids = arrayListOf(1L, 2L, 3L)
        val users = ArrayList<User>()

        repeat(ids.size) {
            users.plusAssign(User(uid = ids[it],
                    name = String(Random.nextBytes(12)),
                    nickname = String(Random.nextBytes(12)),
                    photo = String(Random.nextBytes(12))))
        }

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(userDaoMock.loadContactsIds()).thenReturn(LongArray(0))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactsIdsListDTO>)
                    .coroutine!!
                    .resume(ContactsIdsListDTO().apply { this.error = Errors.EMPTY_CONTACTS_LIST })
        }

        // Testing ...
        val contacts = contactsRepository.fetchContacts()

        // Verifying ...
        Mockito.verify(contactsRepository, Mockito.never()).fetchNewContacts(any())
        Mockito.verify(contactsRepository).invalidateContacts(null)
        Mockito.verify(userDaoMock, Mockito.times(1)).loadContacts()
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
        assertNotNull(contacts)
        assertTrue(contacts!!.isEmpty())
    }

    @Test
    fun testFetchContacts_network_exception() {
        val ids = arrayListOf(1L, 2L, 3L)
        val users = ArrayList<User>()

        repeat(ids.size) {
            users.plusAssign(User(uid = ids[it],
                    name = String(Random.nextBytes(12)),
                    nickname = String(Random.nextBytes(12)),
                    photo = String(Random.nextBytes(12))))
        }

        Mockito.`when`(userDaoMock.loadContacts()).thenReturn(users)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactsIdsListDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing ...
        val contacts = contactsRepository.fetchContacts()

        // Verifying ...
        Mockito.verify(contactsRepository, Mockito.never()).invalidateContacts(any(), ArgumentMatchers.anyBoolean())
        Mockito.verify(contactsRepository, Mockito.never()).fetchNewContacts(any())
        Mockito.verify(userDaoMock).loadContacts()
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
        assertNotNull(contacts)
        assertArrayEquals(users.toTypedArray(), contacts!!.toTypedArray())
    }

    @Test
    fun testFetchContacts_success() {
        val ids = arrayListOf(1L, 2L, 3L)
        val users = ArrayList<User>()

        repeat(ids.size) {
            users.plusAssign(User(uid = ids[it],
                    name = String(Random.nextBytes(12)),
                    nickname = String(Random.nextBytes(12)),
                    photo = String(Random.nextBytes(12))))
        }

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.loadActualUsers(ids)).thenReturn(CompletableDeferred(users))
        Mockito.`when`(userDaoMock.loadContactsIds()).thenReturn(LongArray(0))
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactsIdsListDTO>)
                    .coroutine!!
                    .resume(ContactsIdsListDTO().apply { this.ids = ids })
        }

        // Testing ...
        val contacts = contactsRepository.fetchContacts()

        // Verifying ...
        Mockito.verify(contactsRepository).invalidateContacts(any(), ArgumentMatchers.anyBoolean())
        Mockito.verify(contactsRepository).fetchNewContacts(any())
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
        assertNotNull(contacts)
        assertArrayEquals(users.toTypedArray(), contacts!!.toTypedArray())
    }

    @Test
    fun testFetchContacts_error_during_contact_invalidating() {
        val ids = arrayListOf(1L, 2L, 3L)
        val users = ArrayList<User>()

        repeat(ids.size) {
            users.plusAssign(User(uid = ids[it],
                    name = String(Random.nextBytes(12)),
                    nickname = String(Random.nextBytes(12)),
                    photo = String(Random.nextBytes(12))))
        }

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.loadActualUsers(ids)).thenReturn(CompletableDeferred(ArrayList()))
        Mockito.`when`(userDaoMock.loadContacts()).thenReturn(users)
        Mockito.`when`(userDaoMock.loadContactsIds()).thenReturn(ids.toLongArray())
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactsIdsListDTO>)
                    .coroutine!!
                    .resume(ContactsIdsListDTO().apply { this.error = Errors.INVALID_PARAMETERS })
        }

        // Testing ...
        val contacts = contactsRepository.fetchContacts()

        // Verifying ...
        Mockito.verify(contactsRepository).invalidateContacts(any(), ArgumentMatchers.anyBoolean())
        Mockito.verify(contactsRepository, Mockito.never()).fetchNewContacts(any())
        Mockito.verify(userDaoMock, Mockito.times(2)).loadContacts()
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
        assertNotNull(contacts)
        assertArrayEquals(users.toTypedArray(), contacts!!.toTypedArray())
    }

    @Test
    fun testSyncContacts_contacts_book_empty() = runBlocking {
        PowerMockito.`when`(contextMock.loadContactsFromPhone()).thenReturn(ArrayList())
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactsSyncDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing ...
        try {
            contactsRepository
                    .syncContacts()
                    .await()

            fail("Exception not thrown!")
        } catch (e: InternalRequestException) {
            assertEquals(InternalErrors.CONTACT_BOOK_IS_EMPTY, e.errorCode)
        }
    }

    @Test
    fun testSyncContacts_error_invalid_users() = runBlocking<Unit> {
        val pairs = ArrayList<ContactPairDTO>()
        val users = ArrayList<User>()
        val ids = arrayListOf(1L, 2L, 3L)

        repeat(ids.size) {
            users.plusAssign(User(uid = ids[it],
                    name = String(Random.nextBytes(12)),
                    nickname = String(Random.nextBytes(12)),
                    photo = String(Random.nextBytes(12))))
        }

        repeat(ids.size) {
            pairs.add(ContactPairDTO().apply {
                phone = randomBase64String(8)
                name = randomBase64String(5)
            })
        }

        PowerMockito.`when`(contextMock.loadContactsFromPhone()).thenReturn(pairs)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(userDaoMock.loadContactsIds()).thenReturn(LongArray(0))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactsSyncDTO>)
                    .coroutine!!
                    .resume(ContactsSyncDTO().apply {
                        this.error = Errors.INVALID_USERS
                    })
        }

        contactsRepository
                .contactsChannel
                .offer(users)

        try {
            contactsRepository
                    .syncContacts()
                    .await()

            fail("Exception not throwed!")
        } catch (e: InternalRequestException) {
            Mockito.verify(contactsRepository, Mockito.never()).fetchNewContacts(any())
            Mockito.verify(contactsRepository).invalidateContacts(force = true)
            assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
            assertTrue(contactsRepository.contactsChannel.value.isEmpty())
            assertEquals(InternalErrors.NO_SYNCED_CONTACTS, e.errorCode)
        }
    }

    @Test
    fun testSyncContacts_unknown_error() = runBlocking<Unit> {
        val pairs = ArrayList<ContactPairDTO>()
        val users = ArrayList<User>()
        val ids = arrayListOf(1L, 2L, 3L)

        repeat(ids.size) {
            users.plusAssign(User(uid = ids[it],
                    name = String(Random.nextBytes(12)),
                    nickname = String(Random.nextBytes(12)),
                    photo = String(Random.nextBytes(12))))
        }

        repeat(ids.size) {
            pairs.add(ContactPairDTO().apply {
                phone = randomBase64String(8)
                name = randomBase64String(5)
            })
        }

        PowerMockito.`when`(contextMock.loadContactsFromPhone()).thenReturn(pairs)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(userDaoMock.loadContactsIds()).thenReturn(LongArray(0))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactsSyncDTO>)
                    .coroutine!!
                    .resume(ContactsSyncDTO().apply {
                        this.error = Errors.INVALID_PARAMETERS
                    })
        }

        contactsRepository
                .contactsChannel
                .offer(users)

        try {
            contactsRepository
                    .syncContacts()
                    .await()

            fail("Exception not throwed!")
        } catch (e: RequestException) {
            Mockito.verify(contactsRepository, Mockito.never()).fetchNewContacts(any())
            Mockito.verify(contactsRepository, Mockito.never()).invalidateContacts(force = true)
            assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
            assertArrayEquals(users.toTypedArray(), contactsRepository.contactsChannel.value.toTypedArray())
            assertEquals(Errors.INVALID_PARAMETERS, e.errorCode)
        }
    }

    @Test
    fun testSyncContacts_network_exception() = runBlocking<Unit> {
        val pairs = ArrayList<ContactPairDTO>()
        val ids = arrayListOf(1L, 2L, 3L)

        repeat(ids.size) {
            pairs.add(ContactPairDTO().apply {
                phone = randomBase64String(8)
                name = randomBase64String(5)
            })
        }

        PowerMockito.`when`(contextMock.loadContactsFromPhone()).thenReturn(pairs)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(false)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(false)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactsSyncDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing ...
        val result = contactsRepository
                .syncContacts()
                .await()

        // Verifying ...
        Mockito.verify(contactsRepository, Mockito.never()).fetchNewContacts(any())
        Mockito.verify(contactsRepository, Mockito.never()).invalidateContacts(any(), ArgumentMatchers.anyBoolean())
        assertEquals(ConnectionState.NO_CONNECTION, protocolClientMock.connectionStateChannel.value)
        assertFalse(result)
    }

    @Test
    fun testSyncContacts_success_but_users_not_loaded() = runBlocking<Unit> {
        val pairs = ArrayList<ContactPairDTO>()
        val ids = arrayListOf(1L, 2L, 3L)

        repeat(ids.size) {
            pairs.add(ContactPairDTO().apply {
                phone = randomBase64String(8)
                name = randomBase64String(5)
            })
        }

        PowerMockito.`when`(contextMock.loadContactsFromPhone()).thenReturn(pairs)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(userDaoMock.loadContactsIds()).thenReturn(LongArray(0))
        Mockito.`when`(usersRepositoryMock.loadActualUsers(ids)).thenReturn(CompletableDeferred(ArrayList()))
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactsSyncDTO>)
                    .coroutine!!
                    .resume(ContactsSyncDTO().apply { this.ids = ids })
        }

        // Testing ...
        val result = contactsRepository
                .syncContacts()
                .await()

        // Verifying ...
        Mockito.verify(contactsRepository).fetchNewContacts(ids)
        Mockito.verify(contactsRepository).invalidateContacts(any(), eq(false))
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
        assertNull(contactsRepository.contactsChannel.valueOrNull)
        assertFalse(result)
    }

    @Test
    fun testSyncContacts_success() = runBlocking<Unit> {
        val pairs = ArrayList<ContactPairDTO>()
        val users = ArrayList<User>()
        val ids = arrayListOf(1L, 2L, 3L)

        repeat(ids.size) {
            users.plusAssign(User(uid = ids[it],
                    name = String(Random.nextBytes(12)),
                    nickname = String(Random.nextBytes(12)),
                    photo = String(Random.nextBytes(12))))
        }

        repeat(ids.size) {
            pairs.add(ContactPairDTO().apply {
                phone = randomBase64String(8)
                name = randomBase64String(5)
            })
        }

        PowerMockito.`when`(contextMock.loadContactsFromPhone()).thenReturn(pairs)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(userDaoMock.loadContactsIds()).thenReturn(LongArray(0))
        Mockito.`when`(usersRepositoryMock.loadActualUsers(ids)).thenReturn(CompletableDeferred(users))
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactsSyncDTO>)
                    .coroutine!!
                    .resume(ContactsSyncDTO().apply { this.ids = ids })
        }

        // Testing ...
        val result = contactsRepository
                .syncContacts()
                .await()

        // Verifying ...
        Mockito.verify(contactsRepository).fetchNewContacts(ids)
        Mockito.verify(contactsRepository).invalidateContacts(any(), ArgumentMatchers.anyBoolean())
        assertNull(protocolClientMock.connectionStateChannel.valueOrNull)
        assertArrayEquals(users.toTypedArray(), contactsRepository.contactsChannel.value.toTypedArray())
        assertTrue(result)
    }

    @Test
    fun testAddContact_invalid_name_format() = runBlocking {
        try {
            contactsRepository
                    .addContact("@", "79000000000")
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestRegexException) {
            assertArrayEquals(arrayOf(ContactsRepository.CONTACTS_NAME_REGEX_ERROR), e.fields.toTypedArray())
        }
    }

    @Test
    fun testAddContact_invalid_phone_format() = runBlocking {
        try {
            contactsRepository
                    .addContact("testpool", "9")
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestRegexException) {
            assertArrayEquals(arrayOf(ContactsRepository.CONTACTS_PHONE_REGEX_ERROR), e.fields.toTypedArray())
        }
    }

    @Test
    fun testAddContact_invalid_name_and_phone_format() = runBlocking {
        try {
            contactsRepository
                    .addContact("@", "9")
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestRegexException) {
            assertEquals(2, e.fields.size)
            assertTrue(e.fields.containsAll(arrayListOf(
                    ContactsRepository.CONTACTS_PHONE_REGEX_ERROR,
                    ContactsRepository.CONTACTS_PHONE_REGEX_ERROR)))
        }
    }

    @Test
    fun testAddContact_success() = runBlocking<Unit> {
        val id = Random.nextLong()
        val user = User(uid = id,
                name = String(Random.nextBytes(12)),
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)))

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(id)).thenReturn(CompletableDeferred(user))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactAddDTO>)
                    .coroutine!!
                    .resume(ContactAddDTO().apply {
                        this.id = id
                    })
        }

        // Testing ...
        val result = contactsRepository
                .addContact("testpool", "79000000000")
                .await()

        // Verifying ...
        Mockito.verify(contactsRepository).addContactMark(user)
        Mockito.verify(contactsRepository).notifyContactAdded(user)
        assertTrue(result)
    }

    @Test
    fun testAddContact_empty_name() = runBlocking<Unit> {
        val id = Random.nextLong()
        val user = User(uid = id,
                name = String(Random.nextBytes(12)),
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)))

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(id)).thenReturn(CompletableDeferred(user))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactAddDTO>)
                    .coroutine!!
                    .resume(ContactAddDTO().apply {
                        this.id = id
                    })
        }

        // Testing ...
        val result = contactsRepository
                .addContact("", "79000000000")
                .await()

        // Verifying ...
        Mockito.verify(contactsRepository).addContactMark(user)
        Mockito.verify(contactsRepository).notifyContactAdded(user)
        assertTrue(result)
    }

    @Test
    fun testAddContact_success_but_user_not_loaded() = runBlocking<Unit> {
        val id = Random.nextLong()
        val deferred = CompletableDeferred<User?>(null).apply {
            complete(null)
        }

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(id)).thenReturn(deferred)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactAddDTO>)
                    .coroutine!!
                    .resume(ContactAddDTO().apply {
                        this.id = id
                    })
        }

        // Testing ...
        val result = contactsRepository
                .addContact("testpool", "79000000000")
                .await()

        // Verifying ...
        Mockito.verify(contactsRepository, Mockito.never()).addContactMark(any())
        Mockito.verify(contactsRepository, Mockito.never()).notifyContactAdded(any())
        assertFalse(result)
    }

    @Test
    fun testAddContact_error_user_account_null() = runBlocking<Unit> {
        val id = Random.nextLong()
        val user = User(uid = id,
                name = String(Random.nextBytes(12)),
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)))

        Mockito.`when`(accountRepositoryMock.getAccount()).thenReturn(null)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(id)).thenReturn(CompletableDeferred(user))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactAddDTO>)
                    .coroutine!!
                    .resume(ContactAddDTO().apply {
                        this.error = Errors.INVALID_USER
                    })
        }

        // Testing ...
        val result = contactsRepository
                .addContact("testpool", "79000000000")
                .await()

        // Verifying ...
        Mockito.verify(contactsRepository, Mockito.never()).addContactMark(user)
        Mockito.verify(contactsRepository, Mockito.never()).notifyContactAdded(user)
        assertFalse(result)
    }

    @Test
    fun testAddContact_error_user_account_id_null() = runBlocking<Unit> {
        val id = Random.nextLong()
        val account = Mockito.mock(Account::class.java)
        val user = User(uid = id,
                name = String(Random.nextBytes(12)),
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)))

        Mockito.`when`(accountRepositoryMock.getAccount()).thenReturn(account)
        Mockito.`when`(accountRepositoryMock.getAccountId(account)).thenReturn(null)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(id)).thenReturn(CompletableDeferred(user))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactAddDTO>)
                    .coroutine!!
                    .resume(ContactAddDTO().apply {
                        this.error = Errors.INVALID_USER
                    })
        }

        // Testing ...
        val result = contactsRepository
                .addContact("testpool", "79000000000")
                .await()

        // Verifying ...
        Mockito.verify(contactsRepository, Mockito.never()).addContactMark(user)
        Mockito.verify(contactsRepository, Mockito.never()).notifyContactAdded(user)
        assertFalse(result)
    }

    @Test
    fun testAddContact_error_user_account_user_null() = runBlocking<Unit> {
        val id = Random.nextLong()
        val account = Mockito.mock(Account::class.java)
        val deferred = CompletableDeferred<User?>(null).apply { complete(null) }
        val user = User(uid = id,
                name = String(Random.nextBytes(12)),
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)))

        Mockito.`when`(accountRepositoryMock.getAccount()).thenReturn(account)
        Mockito.`when`(accountRepositoryMock.getAccountId(any())).thenReturn(user.uid)
        Mockito.`when`(usersRepositoryMock.loadUser(user.uid)).thenReturn(deferred)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactAddDTO>)
                    .coroutine!!
                    .resume(ContactAddDTO().apply {
                        this.error = Errors.INVALID_USER
                    })
        }

        // Testing ...
        val result = contactsRepository
                .addContact("testpool", "79000000000")
                .await()

        // Verifying ...
        Mockito.verify(contactsRepository, Mockito.never()).addContactMark(user)
        Mockito.verify(contactsRepository, Mockito.never()).notifyContactAdded(user)
        assertFalse(result)
    }

    @Test
    fun testAddContact_error_user_phone_equals() = runBlocking<Unit> {
        val id = Random.nextLong()
        val account = Mockito.mock(Account::class.java)
        val user = User(uid = id,
                name = String(Random.nextBytes(12)),
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)),
                phone = "79000000000")

        Mockito.`when`(accountRepositoryMock.getAccount()).thenReturn(account)
        Mockito.`when`(accountRepositoryMock.getAccountId(any())).thenReturn(user.uid)
        Mockito.`when`(usersRepositoryMock.loadUser(user.uid)).thenReturn(CompletableDeferred(user))
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(id)).thenReturn(CompletableDeferred(user))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactAddDTO>)
                    .coroutine!!
                    .resume(ContactAddDTO().apply { this.error = Errors.INVALID_USER })
        }

        try {
            contactsRepository
                    .addContact("testpool", "79000000000")
                    .await()

            fail("Exception not thrown!")
        } catch (e: InternalRequestException) {
            Mockito.verify(contactsRepository, Mockito.never()).addContactMark(user)
            Mockito.verify(contactsRepository, Mockito.never()).notifyContactAdded(user)
            assertEquals(InternalErrors.ATTEMPT_TO_ADDING_MYSELF, e.errorCode)
        }
    }

    @Test
    fun testAddContact_error_user_is_contact() = runBlocking<Unit> {
        val id = Random.nextLong()
        val account = Mockito.mock(Account::class.java)
        val user = User(uid = id,
                name = String(Random.nextBytes(12)),
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)),
                phone = "79000000001")

        Mockito.`when`(accountRepositoryMock.getAccount()).thenReturn(account)
        Mockito.`when`(accountRepositoryMock.getAccountId(any())).thenReturn(user.uid)
        Mockito.`when`(usersRepositoryMock.loadUser(user.uid)).thenReturn(CompletableDeferred(user))
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(id)).thenReturn(CompletableDeferred(user))
        Mockito.`when`(userDaoMock.isContactByPhone(any())).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactAddDTO>)
                    .coroutine!!
                    .resume(ContactAddDTO().apply { this.error = Errors.INVALID_USER })
        }

        try {
            contactsRepository
                    .addContact("testpool", "79000000000")
                    .await()

            fail("Exception not thrown!")
        } catch (e: InternalRequestException) {
            Mockito.verify(contactsRepository, Mockito.never()).addContactMark(user)
            Mockito.verify(contactsRepository, Mockito.never()).notifyContactAdded(user)
            assertEquals(InternalErrors.USER_ALREADY_ADDED, e.errorCode)
        }
    }

    @Test
    fun testAddContact_error_invalid_user_not_found() = runBlocking<Unit> {
        val id = Random.nextLong()
        val account = Mockito.mock(Account::class.java)
        val user = User(uid = id,
                name = String(Random.nextBytes(12)),
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)),
                phone = "79000000001")

        Mockito.`when`(accountRepositoryMock.getAccount()).thenReturn(account)
        Mockito.`when`(accountRepositoryMock.getAccountId(any())).thenReturn(user.uid)
        Mockito.`when`(usersRepositoryMock.loadUser(user.uid)).thenReturn(CompletableDeferred(user))
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(id)).thenReturn(CompletableDeferred(user))
        Mockito.`when`(userDaoMock.isContactByPhone(any())).thenReturn(false)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactAddDTO>)
                    .coroutine!!
                    .resume(ContactAddDTO().apply { this.error = Errors.INVALID_USER })
        }

        try {
            contactsRepository
                    .addContact("testpool", "79000000000")
                    .await()

            fail("Exception not thrown!")
        } catch (e: InternalRequestException) {
            Mockito.verify(contactsRepository, Mockito.never()).addContactMark(user)
            Mockito.verify(contactsRepository, Mockito.never()).notifyContactAdded(user)
            assertEquals(InternalErrors.USER_NOT_FOUND, e.errorCode)
        }
    }

    @Test
    fun testAddContact_other_error() = runBlocking<Unit> {
        val id = Random.nextLong()
        val user = User(uid = id,
                name = String(Random.nextBytes(12)),
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)))

        Mockito.`when`(accountRepositoryMock.getAccount()).thenReturn(null)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(id)).thenReturn(CompletableDeferred(user))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactAddDTO>)
                    .coroutine!!
                    .resume(ContactAddDTO().apply {
                        this.error = Errors.INVALID_PARAMETERS
                    })
        }

        try {
            contactsRepository
                    .addContact("testpool", "79000000000")
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestException) {
            Mockito.verify(contactsRepository, Mockito.never()).addContactMark(user)
            Mockito.verify(contactsRepository, Mockito.never()).notifyContactAdded(user)
            assertEquals(Errors.INVALID_PARAMETERS, e.errorCode)
        }
    }

    @Test
    fun testAddContact_network_exception() = runBlocking<Unit> {
        val id = Random.nextLong()
        val user = User(uid = id,
                name = String(Random.nextBytes(12)),
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)))

        Mockito.`when`(accountRepositoryMock.getAccount()).thenReturn(null)
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(id)).thenReturn(CompletableDeferred(user))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactAddDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing ...
        val result = contactsRepository
                .addContact("testpool", "79000000000")
                .await()

        // Verifying ...
        Mockito.verify(contactsRepository, Mockito.never()).addContactMark(user)
        Mockito.verify(contactsRepository, Mockito.never()).notifyContactAdded(user)
        assertFalse(result)
    }

    @Test
    fun testRemoveContact_network_exception() = runBlocking<Unit> {
        val id = Random.nextLong()

        // Preparing ...
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactRemoveDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing ...
        val status = contactsRepository
                .removeContact(id)
                .await()

        // Verifying ...
        Mockito.verify(usersRepositoryMock, Mockito.never()).loadActualUser(id)
        Mockito.verify(contactsRepository, Mockito.never()).removeContactMark(any())
        Mockito.verify(contactsRepository, Mockito.never()).notifyContactRemoved(id)
        assertFalse(status)
    }

    @Test
    fun testRemoveContact_user_null() = runBlocking<Unit> {
        val id = Random.nextLong()
        val deferred = CompletableDeferred<User?>().apply {
            complete(null)
        }

        // Preparing ...
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.loadActualUser(id)).thenReturn(deferred)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactRemoveDTO>)
                    .coroutine!!
                    .resume(ContactRemoveDTO().apply { this.id = id })
        }

        // Testing ...
        val status = contactsRepository
                .removeContact(id)
                .await()

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUser(id)
        Mockito.verify(contactsRepository, Mockito.never()).removeContactMark(any())
        Mockito.verify(contactsRepository, Mockito.never()).notifyContactRemoved(id)
        assertFalse(status)
    }

    @Test
    fun testRemoveContact_success() = runBlocking<Unit> {
        val id = Random.nextLong()
        val user = User(uid = id,
                name = randomBase64String(32),
                nickname = randomBase64String(32),
                photo = "")

        // Preparing ...
        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.loadActualUser(id)).thenReturn(CompletableDeferred(user))
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactRemoveDTO>)
                    .coroutine!!
                    .resume(ContactRemoveDTO().apply { this.id = id })
        }

        // Testing ...
        val status = contactsRepository
                .removeContact(id)
                .await()

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUser(id)
        Mockito.verify(contactsRepository).removeContactMark(any())
        Mockito.verify(contactsRepository).notifyContactRemoved(id)
        assertTrue(status)
    }

    @Test
    fun testEditContact_invalid_name_format() = runBlocking {
        val id = Random.nextLong()
        val user = User(uid = id,
                name = "@",
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)),
                phone = "79000000000")

        try {
            contactsRepository
                    .editContact(user)
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestRegexException) {
            assertArrayEquals(arrayOf(ContactsRepository.CONTACTS_NAME_REGEX_ERROR), e.fields.toTypedArray())
        }
    }

    @Test
    fun testEditContact_success() = runBlocking<Unit> {
        val id = Random.nextLong()
        val user = User(uid = id,
                name = "testpool",
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)))

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(eq(id), eq(false), ArgumentMatchers.anyBoolean())).thenReturn(CompletableDeferred(user))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactEditDTO>)
                    .coroutine!!
                    .resume(ContactEditDTO().apply {
                        this.id = id
                    })
        }

        // Testing ...
        val result = contactsRepository
                .editContact(user)
                .await()

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadUser(id, false, false)
        Mockito.verify(usersRepositoryMock).saveOrUpdateUsers(user)
        Mockito.verify(contactsRepository).notifyContactUpdated(user)
        assertTrue(result)
    }

    @Test
    fun testEditContact_success_empty_name() = runBlocking<Unit> {
        val id = Random.nextLong()
        val user = User(uid = id,
                name = "",
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)))

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(eq(id), eq(false), ArgumentMatchers.anyBoolean())).thenReturn(CompletableDeferred(user))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactEditDTO>)
                    .coroutine!!
                    .resume(ContactEditDTO().apply {
                        this.id = id
                    })
        }

        // Testing ...
        val result = contactsRepository
                .editContact(user)
                .await()

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadUser(id, false, true)
        Mockito.verify(usersRepositoryMock).saveOrUpdateUsers(user)
        Mockito.verify(contactsRepository).notifyContactUpdated(user)
        assertTrue(result)
    }

    @Test
    fun testEditContact_invalid_user_error() = runBlocking<Unit> {
        val id = Random.nextLong()
        val user = User(uid = id,
                name = "testpool",
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)))

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(eq(id), eq(false), ArgumentMatchers.anyBoolean())).thenReturn(CompletableDeferred(user))
        Mockito.`when`(usersRepositoryMock.loadActualUser(ArgumentMatchers.anyLong())).thenReturn(CompletableDeferred(user))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactEditDTO>)
                    .coroutine!!
                    .resume(ContactEditDTO().apply { this.error = Errors.INVALID_USER })
        }

        try {
            contactsRepository
                    .editContact(user)
                    .await()

            fail("Exception not thrown!")
        } catch (e: InternalRequestException) {
            Mockito.verify(usersRepositoryMock, Mockito.never()).saveOrUpdateUsers(user)
            Mockito.verify(contactsRepository, Mockito.never()).notifyContactUpdated(user)
            Mockito.verify(usersRepositoryMock).loadActualUser(id)
            Mockito.verify(contactsRepository).removeContactMark(any())
            Mockito.verify(contactsRepository).notifyContactRemoved(id)
            assertEquals(InternalErrors.USER_NOT_FOUND, e.errorCode)
        }
    }

    @Test
    fun testEditContact_other_error() = runBlocking {
        val id = Random.nextLong()
        val user = User(uid = id,
                name = "testpool",
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)))

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(eq(id), eq(false), ArgumentMatchers.anyBoolean())).thenReturn(CompletableDeferred(user))
        Mockito.`when`(usersRepositoryMock.loadActualUser(ArgumentMatchers.anyLong())).thenReturn(CompletableDeferred(user))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactEditDTO>)
                    .coroutine!!
                    .resume(ContactEditDTO().apply { this.error = Errors.INVALID_PARAMETERS })
        }

        try {
            contactsRepository
                    .editContact(user)
                    .await()

            fail("Exception not thrown!")
        } catch (e: RequestException) {
            Mockito.verify(usersRepositoryMock, Mockito.never()).saveOrUpdateUsers(user)
            Mockito.verify(contactsRepository, Mockito.never()).notifyContactUpdated(user)
            assertEquals(Errors.INVALID_PARAMETERS, e.errorCode)
        }
    }

    @Test
    fun testEditContact_network_exception() = runBlocking {
        val id = Random.nextLong()
        val user = User(uid = id,
                name = "testpool",
                nickname = String(Random.nextBytes(12)),
                photo = String(Random.nextBytes(12)))

        Mockito.`when`(authRepositoryMock.canExecuteNetworkRequest()).thenReturn(true)
        Mockito.`when`(protocolClientMock.isValid()).thenReturn(true)
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))
        Mockito.`when`(usersRepositoryMock.loadUser(eq(id), eq(false), ArgumentMatchers.anyBoolean())).thenReturn(CompletableDeferred(user))
        Mockito.`when`(usersRepositoryMock.loadActualUser(ArgumentMatchers.anyLong())).thenReturn(CompletableDeferred(user))
        Mockito.`when`(protocolClientMock.sendJsonMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any())).then {
            (protocolClientMock
                    .readCallbacks
                    .removeFirst() as ReadCallback<ContactEditDTO>)
                    .coroutine!!
                    .resumeWithException(NetworkException())
        }

        // Testing ...
        val result = contactsRepository
                .editContact(user)
                .await()

        // Verifying ...
        Mockito.verify(usersRepositoryMock, Mockito.never()).saveOrUpdateUsers(user)
        Mockito.verify(contactsRepository, Mockito.never()).notifyContactUpdated(user)
        assertFalse(result)
    }

    @Test
    fun testFetchNewContacts_empty_ids_list() {
        val ids = arrayListOf<Long>()
        val result = contactsRepository.fetchNewContacts(ids)

        // Verifying ...
        Mockito.verify(usersRepositoryMock, Mockito.never()).loadActualUsers(ids)
        Mockito.verify(contactsRepository, Mockito.never()).addContactMark(any())
        assertNotNull(result)
        assertTrue(result!!.isEmpty())
    }

    @Test
    fun testFetchNewContacts_users_not_loaded() {
        val ids = arrayListOf(1L, 2L)

        // Preparing ...
        Mockito.`when`(usersRepositoryMock.loadActualUsers(ids))
                .thenReturn(CompletableDeferred(ArrayList()))

        // Testing ...
        val result = contactsRepository.fetchNewContacts(ids)

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUsers(ids)
        Mockito.verify(contactsRepository, Mockito.never()).addContactMark(any())
        assertNull(result)
    }

    @Test
    fun testFetchNewContacts_success() {
        val ids = arrayListOf(1L, 2L)
        val users = ArrayList<User>()

        repeat(ids.size) {
            users.plusAssign(User(
                    uid = Random.nextLong(),
                    name = randomBase64String(32),
                    nickname = randomBase64String(16),
                    photo = ""
            ))
        }

        Mockito.`when`(usersRepositoryMock.loadActualUsers(ids)).thenReturn(CompletableDeferred(users))
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))

        // Testing ...
        val result = contactsRepository.fetchNewContacts(ids)

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUsers(ids)
        Mockito.verify(contactsRepository).addContactMark(any())
        assertNotNull(result)
        assertArrayEquals(users.toTypedArray(), result!!.toTypedArray())
    }

    @Test
    fun testFetchNewContacts_error_during_invalidating() {
        val ids = arrayListOf(1L, 2L)
        val users = ArrayList<User>()

        repeat(ids.size) {
            users.plusAssign(User(
                    uid = Random.nextLong(),
                    name = randomBase64String(32),
                    nickname = randomBase64String(16),
                    photo = ""
            ))
        }

        Mockito.`when`(usersRepositoryMock.loadActualUsers(ids)).thenReturn(CompletableDeferred(ArrayList()))
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any())).thenReturn(CompletableDeferred(Unit))

        // Testing ...
        val result = contactsRepository.fetchNewContacts(ids)

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUsers(ids)
        Mockito.verify(contactsRepository, Mockito.never()).addContactMark(any())
        assertNull(result)
    }

    @Test
    fun testInvalidateContacts_no_contacts_in_database() {
        Mockito.`when`(userDaoMock.loadContacts()).thenReturn(ArrayList())

        // Testing ...
        val result = contactsRepository.invalidateContacts()

        // Verifying ...
        Mockito.verify(usersRepositoryMock, Mockito.never()).loadActualUsers(any())
        Mockito.verify(contactsRepository, Mockito.never()).removeContactMark(any())
        assertTrue(result)
    }

    @Test
    fun testInvalidateContacts_no_contacts_removed() {
        val ids = arrayListOf(1L)
        val users = ArrayList<User>()

        repeat(ids.size) {
            users.plusAssign(User(
                    uid = ids[it],
                    name = randomBase64String(32),
                    nickname = randomBase64String(16),
                    photo = ""
            ))
        }

        Mockito.`when`(userDaoMock.loadContacts()).thenReturn(users)

        // Testing ...
        val result = contactsRepository.invalidateContacts(arrayListOf(1L, 2L))

        // Verifying ...
        Mockito.verify(usersRepositoryMock, Mockito.never()).loadActualUsers(any())
        Mockito.verify(contactsRepository, Mockito.never()).removeContactMark(any())
        assertTrue(result)
    }

    @Test
    fun testInvalidate_some_contacts_removed() {
        val users = ArrayList<User>()

        repeat(1) {
            users.plusAssign(User(
                    uid = 2L,
                    name = randomBase64String(32),
                    nickname = randomBase64String(16),
                    photo = ""
            ))
        }

        Mockito.`when`(userDaoMock.loadContacts()).thenReturn(users)
        Mockito.`when`(usersRepositoryMock.loadActualUsers(arrayListOf(2L)))
                .thenReturn(CompletableDeferred(users))

        // Testing ...
        val result = contactsRepository.invalidateContacts(arrayListOf(1L))

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUsers(arrayListOf(2L))
        Mockito.verify(contactsRepository).removeContactMark(*users.toTypedArray())
        assertTrue(result)
    }

    @Test
    fun testInvalidate_all_contacts_removed() {
        val users = ArrayList<User>()

        repeat(1) {
            users.plusAssign(User(
                    uid = 2L,
                    name = randomBase64String(32),
                    nickname = randomBase64String(16),
                    photo = ""
            ))
        }

        Mockito.`when`(userDaoMock.loadContacts()).thenReturn(users)
        Mockito.`when`(usersRepositoryMock.loadActualUsers(arrayListOf(2L)))
                .thenReturn(CompletableDeferred(users))

        // Testing ...
        val result = contactsRepository.invalidateContacts()

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUsers(arrayListOf(2L))
        Mockito.verify(contactsRepository).removeContactMark(*users.toTypedArray())
        assertTrue(result)
    }

    @Test
    fun testInvalidate_all_contacts_removed_but_users_not_loaded() {
        val users = ArrayList<User>()

        repeat(1) {
            users.plusAssign(User(
                    uid = 2L,
                    name = randomBase64String(32),
                    nickname = randomBase64String(16),
                    photo = ""
            ))
        }

        Mockito.`when`(userDaoMock.loadContacts()).thenReturn(users)
        Mockito.`when`(usersRepositoryMock.loadActualUsers(arrayListOf(2L)))
                .thenReturn(CompletableDeferred(ArrayList()))

        // Testing ...
        val result = contactsRepository.invalidateContacts()

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUsers(arrayListOf(2L))
        Mockito.verify(contactsRepository, Mockito.never()).removeContactMark(*users.toTypedArray())
        assertFalse(result)
    }

    @Test
    fun testInvalidate_all_contacts_removed_but_users_not_loaded_with_force_flag() {
        val users = ArrayList<User>()

        repeat(1) {
            users.plusAssign(User(
                    uid = 2L,
                    name = randomBase64String(32),
                    nickname = randomBase64String(16),
                    photo = ""
            ))
        }

        Mockito.`when`(userDaoMock.loadContacts()).thenReturn(users)
        Mockito.`when`(usersRepositoryMock.loadActualUsers(arrayListOf(2L)))
                .thenReturn(CompletableDeferred(ArrayList()))

        // Testing ...
        val result = contactsRepository.invalidateContacts(force = true)

        // Verifying ...
        Mockito.verify(usersRepositoryMock).loadActualUsers(arrayListOf(2L))
        Mockito.verify(contactsRepository).removeContactMark(*users.toTypedArray())
        assertFalse(result)
    }

    @Test
    fun testAddContactMark() {
        val users = ArrayList<User>()
        val contactsIds = arrayListOf(1L, 2L, 3L, 4L)
        val othersUsersIds = arrayListOf(5L, 6L, 7L, 8L)

        repeat(contactsIds.size) {
            users.plusAssign(User(
                    uid = Random.nextLong(),
                    name = randomBase64String(32),
                    nickname = randomBase64String(16),
                    photo = randomBase64String(32),
                    isContact = true
            ))
        }

        repeat(othersUsersIds.size) {
            users.plusAssign(User(
                    uid = Random.nextLong(),
                    name = randomBase64String(32),
                    nickname = randomBase64String(16),
                    photo = randomBase64String(32),
                    isContact = false
            ))
        }

        // Preparing ...
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any()))
                .thenReturn(CompletableDeferred(Unit))

        // Testing ...
        contactsRepository.addContactMark(*users.toTypedArray())

        // Verifying ...
        Mockito.verify(usersRepositoryMock).saveOrUpdateUsers(*users
                .subList(contactsIds.size, users.lastIndex + 1)
                .toTypedArray())
    }

    @Test
    fun testAddContactMark_none() {
        val users = ArrayList<User>()
        val othersUsersIds = arrayListOf(5L, 6L, 7L, 8L)

        repeat(othersUsersIds.size) {
            users.plusAssign(User(
                    uid = Random.nextLong(),
                    name = randomBase64String(32),
                    nickname = randomBase64String(16),
                    photo = randomBase64String(32),
                    isContact = true
            ))
        }

        // Preparing ...
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any()))
                .thenReturn(CompletableDeferred(Unit))

        // Testing ...
        contactsRepository.addContactMark(*users.toTypedArray())

        // Verifying ...
        Mockito.verify(usersRepositoryMock, Mockito.never()).saveOrUpdateUsers(any())
    }

    @Test
    fun testRemoveContactMark() {
        val users = ArrayList<User>()
        val contactsIds = arrayListOf(1L, 2L, 3L, 4L)
        val othersUsersIds = arrayListOf(5L, 6L, 7L, 8L)

        repeat(contactsIds.size) {
            users.plusAssign(User(
                    uid = Random.nextLong(),
                    name = randomBase64String(32),
                    nickname = randomBase64String(16),
                    photo = randomBase64String(32),
                    isContact = true
            ))
        }

        repeat(othersUsersIds.size) {
            users.plusAssign(User(
                    uid = Random.nextLong(),
                    name = randomBase64String(32),
                    nickname = randomBase64String(16),
                    photo = randomBase64String(32),
                    isContact = false
            ))
        }

        // Preparing ...
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any()))
                .thenReturn(CompletableDeferred(Unit))

        // Testing ...
        contactsRepository.removeContactMark(*users.toTypedArray())

        // Verifying ...
        Mockito.verify(usersRepositoryMock).saveOrUpdateUsers(*users
                .subList(0, othersUsersIds.size)
                .toTypedArray())
    }

    @Test
    fun testRemoveContactMark_none() {
        val users = ArrayList<User>()
        val contactsIds = arrayListOf(5L, 6L, 7L, 8L)

        repeat(contactsIds.size) {
            users.plusAssign(User(
                    uid = Random.nextLong(),
                    name = randomBase64String(32),
                    nickname = randomBase64String(16),
                    photo = randomBase64String(32),
                    isContact = false
            ))
        }

        // Preparing ...
        Mockito.`when`(usersRepositoryMock.saveOrUpdateUsers(any()))
                .thenReturn(CompletableDeferred(Unit))

        // Testing ...
        contactsRepository.removeContactMark(*users.toTypedArray())

        // Verifying ...
        Mockito.verify(usersRepositoryMock, Mockito.never()).saveOrUpdateUsers(any())
    }

    @Test
    fun testNotifyContactAdded() {
        val user = User(
                uid = Random.nextLong(),
                name = randomBase64String(32),
                nickname = randomBase64String(16),
                photo = randomBase64String(32),
                isContact = true)

        // Testing ...
        contactsRepository.contactsChannel.offer(ArrayList())
        contactsRepository.notifyContactAdded(user)

        // Verifying ...
        assertEquals(1, contactsRepository.contactsChannel.value.size)
        assertTrue(contactsRepository.contactsChannel.value.contains(user))
    }

    @Test
    fun testNotifyContactUpdated() {
        val user = User(
                uid = Random.nextLong(),
                name = randomBase64String(32),
                nickname = randomBase64String(16),
                photo = randomBase64String(32),
                isContact = true)

        val updatedUser = User(
                uid = user.uid,
                name = randomBase64String(32),
                nickname = randomBase64String(16),
                photo = randomBase64String(32),
                isContact = true
        )

        // Testing ...
        contactsRepository.contactsChannel.offer(arrayListOf(user))
        contactsRepository.notifyContactUpdated(updatedUser)

        // Verifying ...
        assertEquals(1, contactsRepository.contactsChannel.value.size)
        assertTrue(contactsRepository.contactsChannel.value.contains(updatedUser))
    }

    @Test
    fun testNotifyContactRemoved() {
        val user = User(
                uid = Random.nextLong(),
                name = randomBase64String(32),
                nickname = randomBase64String(16),
                photo = randomBase64String(32),
                isContact = true)

        // Testing ...
        contactsRepository.contactsChannel.offer(arrayListOf(user))
        contactsRepository.notifyContactRemoved(user.uid)

        // Verifying ...
        assertEquals(0, contactsRepository.contactsChannel.value.size)
    }
}