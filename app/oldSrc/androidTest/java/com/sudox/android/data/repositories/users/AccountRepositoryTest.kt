package com.sudox.android.data.repositories.users

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Build
import androidx.test.InstrumentationRegistry
import androidx.test.filters.MediumTest
import androidx.test.filters.SdkSuppress
import androidx.test.runner.AndroidJUnit4
import com.sudox.android.data.database.SudoxDatabase
import com.sudox.android.data.database.model.user.User
import com.sudox.android.tests.helpers.randomBase64String
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.util.concurrent.Semaphore
import kotlin.random.Random

@MediumTest
@RunWith(AndroidJUnit4::class)
class AccountRepositoryTest : Assert() {

    private lateinit var accountRepository: AccountRepository
    private lateinit var accountManager: AccountManager
    private lateinit var database: SudoxDatabase

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getContext()

        // Create mocks
        database = Mockito.mock(SudoxDatabase::class.java)
        accountManager = AccountManager.get(context)
        accountRepository = AccountRepository(
                database = database,
                context = context)

        // Mock methods
        Mockito.doNothing().`when`(database).clearAllTables()
    }

    @After
    fun tearDown() {
        accountManager
                .getAccountsByType(AccountRepository.ACCOUNT_TYPE)
                .forEach {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
                        val semaphore = Semaphore(0)

                        @Suppress("DEPRECATION")
                        accountManager.removeAccount(it!!, {
                            semaphore.release()
                        }, null)

                        // Waiting where operation will finished
                        semaphore.acquire()
                    } else {
                        accountManager.removeAccountExplicitly(it!!)
                    }
                }
    }

    @Test
    fun testSaveAccount_normal() {
        val token = randomBase64String(64)
        val user = User(
                uid = Random.nextLong(),
                name = randomBase64String(32),
                nickname = randomBase64String(8),
                photo = randomBase64String(32))

        // Testing (try duplicate data) ...
        accountRepository.saveOrUpdateAccount(token, user)

        // Verifying ...
        val accounts = accountManager
                .accounts
                .filter { it.type == AccountRepository.ACCOUNT_TYPE }

        assertEquals(1, accounts.size)

        val account = accounts.lastOrNull()
        val accountId = accountManager.getUserData(account, AccountRepository.KEY_ACCOUNT_ID)
        val accountName = accountManager.getUserData(account, AccountManager.KEY_ACCOUNT_NAME)
        val accountToken = accountManager.getUserData(account, AccountManager.KEY_AUTHTOKEN)

        Mockito.verify(database).clearAllTables()
        assertEquals(user.uid, accountId.toLong())
        assertEquals(user.nickname, accountName)
        assertEquals(token, accountToken)
    }

    @Test
    fun testSaveAccount_normal_with_unused_accounts() {
        val token = randomBase64String(64)
        val user = User(
                uid = Random.nextLong(),
                name = randomBase64String(32),
                nickname = randomBase64String(8),
                photo = randomBase64String(32))

        // Testing ...
        accountRepository.saveOrUpdateAccount(randomBase64String(64), User(
                uid = Random.nextLong(),
                name = randomBase64String(32),
                nickname = randomBase64String(8),
                photo = randomBase64String(32)))

        Mockito.reset(database)
        accountRepository.saveOrUpdateAccount(token, user)

        // Verifying ...
        val accounts = accountManager
                .accounts
                .filter { it.type == AccountRepository.ACCOUNT_TYPE }

        assertEquals(1, accounts.size)

        val account = accounts.lastOrNull()
        val accountId = accountManager.getUserData(account, AccountRepository.KEY_ACCOUNT_ID)
        val accountName = accountManager.getUserData(account, AccountManager.KEY_ACCOUNT_NAME)
        val accountToken = accountManager.getUserData(account, AccountManager.KEY_AUTHTOKEN)

        Mockito.verify(database).clearAllTables()
        assertEquals(user.uid, accountId.toLong())
        assertEquals(user.nickname, accountName)
        assertEquals(token, accountToken)
    }

    @Test
    fun testSaveAccount_name_duplicates() {
        val token = randomBase64String(64)
        val user = User(
                uid = Random.nextLong(),
                name = randomBase64String(32),
                nickname = randomBase64String(8),
                photo = randomBase64String(32))

        // Testing (try duplicate data) ...
        accountRepository.saveOrUpdateAccount(randomBase64String(64), user)
        Mockito.reset(database)
        accountRepository.saveOrUpdateAccount(token, user)

        // Verifying ...
        val accounts = accountManager
                .accounts
                .filter { it.type == AccountRepository.ACCOUNT_TYPE }

        assertEquals(1, accounts.size)

        val account = accounts.lastOrNull()
        val accountId = accountManager.getUserData(account, AccountRepository.KEY_ACCOUNT_ID)
        val accountName = accountManager.getUserData(account, AccountManager.KEY_ACCOUNT_NAME)
        val accountToken = accountManager.getUserData(account, AccountManager.KEY_AUTHTOKEN)

        Mockito.verify(database).clearAllTables()
        assertEquals(user.uid, accountId.toLong())
        assertEquals(user.nickname, accountName)
        assertEquals(token, accountToken)
    }

    @Test
    @SdkSuppress(maxSdkVersion = 21)
    fun testRemoveAccount_21_and_lower_api_single() {
        accountManager.addAccountExplicitly(Account(randomBase64String(32), AccountRepository.ACCOUNT_TYPE), null, null)

        val accounts = accountManager
                .accounts
                .filter { it.type == AccountRepository.ACCOUNT_TYPE }

        // Testing ...
        accountRepository.removeAccount(accounts.first())

        // Verifying ...
        assertEquals(0, accountManager.accounts.filter { it.type == AccountRepository.ACCOUNT_TYPE }.size)
    }

    @Test
    @SdkSuppress(minSdkVersion = 22)
    fun testRemoveAccount_22_and_greater() {
        accountManager.addAccountExplicitly(Account(randomBase64String(32), AccountRepository.ACCOUNT_TYPE), null, null)

        val accounts = accountManager
                .accounts
                .filter { it.type == AccountRepository.ACCOUNT_TYPE }

        // Testing ...
        accountRepository.removeAccount(accounts.first())

        // Verifying ...
        assertEquals(0, accountManager.accounts.filter { it.type == AccountRepository.ACCOUNT_TYPE }.size)
    }

    @Test
    fun testRemoveAccounts() {
        accountManager.addAccountExplicitly(Account("Test", AccountRepository.ACCOUNT_TYPE), null, null)
        accountManager.addAccountExplicitly(Account(randomBase64String(32), AccountRepository.ACCOUNT_TYPE), null, null)

        // Testing ...
        accountRepository.removeAccounts()

        // Verifying ...
        Mockito.verify(database).clearAllTables()
        assertEquals(0, accountManager.accounts.filter { it.type == AccountRepository.ACCOUNT_TYPE }.size)
    }

    @Test
    fun testRemoveUnusedAccounts() {
        accountManager.addAccountExplicitly(Account("Test", AccountRepository.ACCOUNT_TYPE), null, null)
        accountManager.addAccountExplicitly(Account(randomBase64String(32), AccountRepository.ACCOUNT_TYPE), null, null)

        // Testing ...
        accountRepository.removeUnusedAccounts("Test")

        // Verifying
        val accounts = accountManager.accounts.filter { it.type == AccountRepository.ACCOUNT_TYPE }

        assertEquals(1, accounts.size)
        assertEquals("Test", accounts[0].name)
    }

    @Test
    fun testUpdateAccount() {
        accountRepository.saveOrUpdateAccount(randomBase64String(64), User(
                uid = Random.nextLong(),
                name = randomBase64String(32),
                nickname = randomBase64String(8),
                photo = randomBase64String(32)))

        // Testing ...
        val account = accountManager.accounts.first { it.type == AccountRepository.ACCOUNT_TYPE }
        accountRepository.updateAccount(account, User(45, "Name", "@nickname", "photo"))

        // Verifying
        assertEquals("@nickname", accountManager.getUserData(account, AccountManager.KEY_ACCOUNT_NAME))
        assertEquals(45L, accountManager.getUserData(account, AccountRepository.KEY_ACCOUNT_ID).toLongOrNull())
    }

    @Test
    fun testGetAccount_no_accounts() {
        assertNull(accountRepository.getAccount())
    }

    @Test
    fun testGetAccount_single() {
        accountManager.addAccountExplicitly(Account("Anton", AccountRepository.ACCOUNT_TYPE), null, null)

        // Testing ...
        val account = accountRepository.getAccount()

        // Verifying
        assertNotNull(account)
        assertEquals("Anton", account!!.name)
    }

    @Test
    fun testGetAccount_many() {
        accountManager.addAccountExplicitly(Account("Ivan", AccountRepository.ACCOUNT_TYPE), null, null)
        accountManager.addAccountExplicitly(Account("Anton", AccountRepository.ACCOUNT_TYPE), null, null)

        // Testing ...
        val account = accountRepository.getAccount()

        // Verifying
        assertNotNull(account)
        assertEquals("Anton", account!!.name)
    }

    @Test
    fun testGetAccountId() {
        val id = Random.nextLong()

        // Testing ...
        accountRepository.saveOrUpdateAccount(randomBase64String(64), User(
                uid = id,
                name = randomBase64String(32),
                nickname = randomBase64String(8),
                photo = randomBase64String(32)))

        val account = accountManager.accounts.first { it.type == AccountRepository.ACCOUNT_TYPE }
        val returnedId = accountRepository.getAccountId(account)

        // Verifying ...
        assertEquals(id, returnedId)
    }

    @Test
    fun testGetAccountToken() {
        val token = randomBase64String(64)

        // Testing ...
        accountRepository.saveOrUpdateAccount(token, User(
                uid = Random.nextLong(),
                name = randomBase64String(32),
                nickname = randomBase64String(8),
                photo = randomBase64String(32)))

        val account = accountManager.accounts.first { it.type == AccountRepository.ACCOUNT_TYPE }
        val returnedToken = accountRepository.getAccountToken(account)

        // Verifying ...
        assertEquals(token, returnedToken)
    }
}