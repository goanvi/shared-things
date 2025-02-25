package se.itmo.ru.sharedthings.provider

import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.repository.AccountRepository
import java.util.*


class AccountRepositoryProviderTest {

    private lateinit var accountRepository: AccountRepository

    private lateinit var accountRepositoryProvider: AccountRepositoryProvider

    @BeforeEach
    fun setUp() {
        accountRepository = mock(AccountRepository::class.java)
        accountRepositoryProvider = AccountRepositoryProvider(accountRepository)
    }


    @Test
    fun `test saveAccount with new account`() {
        val account = Account(accountId = 0, username = "testUser", email = "test@example.com")

        `when`(accountRepository.existsById(anyInt())).thenReturn(false)
        `when`(accountRepository.existsByUsername(anyString())).thenReturn(false)
        `when`(accountRepository.existsByEmail(anyString())).thenReturn(false)
        `when`(accountRepository.save(any(Account::class.java))).thenReturn(account)

        val savedAccount = accountRepositoryProvider.saveAccount(account)

        assertNotNull(savedAccount)
        assertEquals(account, savedAccount)
        verify(accountRepository, times(1)).save(account)
    }

    @Test
    fun `test saveAccount with existing accountId`() {
        val account = Account(accountId = 1, username = "testUser", email = "test@example.com")

        `when`(accountRepository.existsById(anyInt())).thenReturn(true)

        assertThrows(EntityExistsException::class.java) {
            accountRepositoryProvider.saveAccount(account)
        }
    }

    @Test
    fun `test saveAccount with existing username`() {
        val account = Account(accountId = 0, username = "existingUser", email = "test@example.com")

        `when`(accountRepository.existsById(anyInt())).thenReturn(false)
        `when`(accountRepository.existsByUsername(anyString())).thenReturn(true)

        assertThrows(EntityExistsException::class.java) {
            accountRepositoryProvider.saveAccount(account)
        }
    }

    @Test
    fun `test saveAccount with existing email`() {
        val account = Account(accountId = 0, username = "testUser", email = "existing@example.com")

        `when`(accountRepository.existsById(anyInt())).thenReturn(false)
        `when`(accountRepository.existsByUsername(anyString())).thenReturn(false)
        `when`(accountRepository.existsByEmail(anyString())).thenReturn(true)

        assertThrows(EntityExistsException::class.java) {
            accountRepositoryProvider.saveAccount(account)
        }
    }

    @Test
    fun `test updateAccount with valid account`() {
        val accountId = 1
        val existingAccount = Account(accountId = accountId, username = "oldUser", email = "old@example.com")
        val updatedAccount = Account(accountId = 0, username = "newUser", email = "new@example.com")

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount))
        `when`(accountRepository.existsByUsername(updatedAccount.username)).thenReturn(false)
        `when`(accountRepository.existsByEmail(updatedAccount.email!!)).thenReturn(false)
        `when`(accountRepository.save(any(Account::class.java))).thenReturn(updatedAccount)

        val result = accountRepositoryProvider.updateAccount(accountId, updatedAccount)

        assertNotNull(result)
        assertEquals(accountId, result.accountId)
        assertEquals(updatedAccount.username, result.username)
        assertEquals(updatedAccount.email, result.email)
        verify(accountRepository, times(1)).save(updatedAccount)
    }

    @Test
    fun `test updateAccount with non-existing account`() {
        val accountId = 1
        val updatedAccount = Account(accountId = 0, username = "newUser", email = "new@example.com")

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.empty())

        assertThrows(EntityNotFoundException::class.java) {
            accountRepositoryProvider.updateAccount(accountId, updatedAccount)
        }
    }

    @Test
    fun `test updateAccount with existing username`() {
        val accountId = 1
        val existingAccount = Account(accountId = accountId, username = "oldUser", email = "old@example.com")
        val updatedAccount = Account(accountId = 0, username = "existingUser", email = "new@example.com")

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount))
        `when`(accountRepository.existsByUsername(updatedAccount.username)).thenReturn(true)

        assertThrows(EntityExistsException::class.java) {
            accountRepositoryProvider.updateAccount(accountId, updatedAccount)
        }
    }

    @Test
    fun `test updateAccount with existing email`() {
        val accountId = 1
        val existingAccount = Account(accountId = accountId, username = "oldUser", email = "old@example.com")
        val updatedAccount = Account(accountId = 0, username = "newUser", email = "existing@example.com")

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount))
        `when`(accountRepository.existsByUsername(updatedAccount.username)).thenReturn(false)
        `when`(accountRepository.existsByEmail(updatedAccount.email!!)).thenReturn(true)

        assertThrows(EntityExistsException::class.java) {
            accountRepositoryProvider.updateAccount(accountId, updatedAccount)
        }
    }

    @Test
    fun `test getAccountById with existing account`() {
        val accountId = 1
        val account = Account(accountId = accountId, username = "testUser", email = "test@example.com")

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(account))

        val result = accountRepositoryProvider.getAccountById(accountId)

        assertNotNull(result)
        assertEquals(account, result)
    }

    @Test
    fun `test getAccountById with non-existing account`() {
        val accountId = 1

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.empty())

        assertThrows(EntityNotFoundException::class.java) {
            accountRepositoryProvider.getAccountById(accountId)
        }
    }

    @Test
    fun `test getAllUnmoderatedAccount`() {
        val pageable = PageRequest.of(0, 10)
        val accounts = listOf(
            Account(accountId = 1, username = "user1", email = "user1@example.com"),
            Account(accountId = 2, username = "user2", email = "user2@example.com")
        )

        `when`(accountRepository.findAllByModerated(false, pageable)).thenReturn(accounts)

        val result = accountRepositoryProvider.getAllUnmoderatedAccount(pageable)

        assertNotNull(result)
        assertEquals(accounts.size, result.size)
        assertEquals(accounts, result)
    }

    @Test
    fun `test setAccountsAsModerated`() {
        val accountIds = setOf(1, 2, 3)

        accountRepositoryProvider.setAccountsAsModerated(accountIds)

        verify(accountRepository, times(1)).setAccountsAsModerated(accountIds)
    }


}