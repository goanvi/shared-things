package se.itmo.ru.sharedthings.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.data.domain.PageRequest
import se.itmo.ru.sharedthings.dto.AccountDto
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.provider.AccountRepositoryProvider

class AccountServiceTest {

    private lateinit var accountProvider: AccountRepositoryProvider
    private lateinit var accountService: AccountService

    @BeforeEach
    fun setUp() {
        accountProvider = mock(AccountRepositoryProvider::class.java)
        accountService = AccountService(accountProvider)
    }

    @Test
    fun `test createAccount`() {
        val accountDto = AccountDto(accountId = 0, username = "testUser")
        val account = accountDto.toEntity()

        `when`(accountProvider.saveAccount(account)).thenReturn(account)

        val createdAccountDto = accountService.createAccount(accountDto)

        assertNotNull(createdAccountDto)
        assertEquals(accountDto.username, createdAccountDto.username)
        assertEquals(accountDto.email, createdAccountDto.email)
        verify(accountProvider, times(1)).saveAccount(account)
    }

    @Test
    fun `test getAccountById`() {
        val accountId = 1
        val account = Account(accountId = accountId, username = "testUser", email = "test@example.com")

        `when`(accountProvider.getAccountById(accountId)).thenReturn(account)

        val accountDto = accountService.getAccountById(accountId)

        assertNotNull(accountDto)
        assertEquals(account.accountId, accountDto.accountId)
        assertEquals(account.username, accountDto.username)
        assertEquals(account.email, accountDto.email)
        verify(accountProvider, times(1)).getAccountById(accountId)
    }

    @Test
    fun `test getAllUnmoderatedAccount`() {
        val pageable = PageRequest.of(0, 10)
        val accounts = listOf(
            Account(accountId = 1, username = "user1", email = "user1@example.com"),
            Account(accountId = 2, username = "user2", email = "user2@example.com")
        )

        `when`(accountProvider.getAllUnmoderatedAccount(pageable)).thenReturn(accounts)

        val result = accountService.getAllUnmoderatedAccount(pageable)

        assertNotNull(result)
        assertEquals(accounts.size, result.size)
        assertEquals(accounts.map { it.toDto() }, result)
        verify(accountProvider, times(1)).getAllUnmoderatedAccount(pageable)
    }

    @Test
    fun `test setAccountsAsModerated`() {
        val accountIds = setOf(1, 2, 3)

        `when`(accountProvider.setAccountsAsModerated(accountIds)).thenReturn(accountIds.size)

        val result = accountService.setAccountsAsModerated(accountIds)

        assertEquals(accountIds.size, result)
        verify(accountProvider, times(1)).setAccountsAsModerated(accountIds)
    }

    @Test
    fun `test updateAccount`() {
        val accountId = 1
        val accountDto = AccountDto(accountId = accountId, username = "updatedUser", email = "updated@example.com")
        val account = accountDto.toEntity()

        accountService.updateAccount(accountId, accountDto)

        verify(accountProvider, times(1)).updateAccount(accountId, account)
    }

    private fun AccountDto.toEntity(): Account =
        Account(
            accountId = accountId,
            username = username,
            name = name,
            surname = surname,
            email = email,
            moderated = moderated,
        )

    private fun Account.toDto(): AccountDto =
        AccountDto(
            accountId = accountId,
            username = username,
            name = name,
            surname = surname,
            email = email,
            moderated = moderated,
        )
}