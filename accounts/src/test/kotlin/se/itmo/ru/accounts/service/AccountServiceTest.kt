package se.itmo.ru.accounts.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.springframework.data.domain.PageRequest
import se.itmo.ru.common.dto.AccountDto
import se.itmo.ru.accounts.entity.Account
import se.itmo.ru.accounts.provider.AccountRepositoryProvider
import java.util.*

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
        val accountDto = AccountDto(accountId = UUID.randomUUID(), username = "testUser")
        val account = accountDto.toEntity()

        `when`(accountProvider.saveAccount(anyOrNull())).thenReturn(account)

        val createdAccountDto = accountService.createAccount(accountDto)

        assertNotNull(createdAccountDto)
        assertEquals(accountDto.username, createdAccountDto.username)
        assertEquals(accountDto.email, createdAccountDto.email)
    }

    @Test
    fun `test getAccountById`() {
        val accountId = UUID.randomUUID()
        val account = Account(accountId = accountId, username = "testUser", email = "test@example.com")

        `when`(accountProvider.getAccountById(anyOrNull())).thenReturn(account)

        val accountDto = accountService.getAccountById(accountId)

        assertNotNull(accountDto)
        assertEquals(account.accountId, accountDto.accountId)
        assertEquals(account.username, accountDto.username)
        assertEquals(account.email, accountDto.email)
    }

    @Test
    fun `test getAllUnmoderatedAccount`() {
        val pageable = PageRequest.of(0, 10)
        val accounts = listOf(
            Account(accountId = UUID.randomUUID(), username = "user1", email = "user1@example.com"),
            Account(accountId = UUID.randomUUID(), username = "user2", email = "user2@example.com")
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
        val accountIds = setOf(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())

        `when`(accountProvider.setAccountsAsModerated(accountIds)).thenReturn(accountIds.size)

        val result = accountService.setAccountsAsModerated(accountIds)

        assertEquals(accountIds.size, result)
        verify(accountProvider, times(1)).setAccountsAsModerated(accountIds)
    }

    @Test
    fun `test updateAccount`() {
        val accountId = UUID.randomUUID()
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