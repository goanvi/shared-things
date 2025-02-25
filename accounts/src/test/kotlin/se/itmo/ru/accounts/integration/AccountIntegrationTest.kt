package se.itmo.ru.accounts.integration

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import se.itmo.ru.accounts.AbstractIntegrationTest
import se.itmo.ru.accounts.service.AccountService
import se.itmo.ru.common.dto.AccountDto
import java.util.*
import kotlin.random.Random.Default.nextInt
import kotlin.test.assertEquals

class AccountIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var accountService: AccountService

    @Test
    fun `create account should return 200`() {
        //given
        val accountDto =
            AccountDto(
                accountId = UUID.randomUUID(),
                username = UUID.randomUUID().toString(),
                email = "${nextInt(1, 10000)}@example.com"
            )

        //when
        mockMvc.perform(
            post("/account/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto))
                .header("X-User-Id", "baef6ba1-dc19-442e-a681-151c486190a4")
                .header("X-User-Role", "ADMIN")
        ).andExpect(status().isOk)

        //then
        jdbcTemplate.query(
            "SELECT username,email FROM account WHERE username = :username",
            mapOf("username" to accountDto.username)
        ) { r, _ ->
            assertEquals(1, r.row)
            assertEquals(accountDto.username, r.getString("username"))
            assertEquals(accountDto.email, r.getString("email"))
        }
    }

    @Test
    fun `get account by id should return 200`() {
        // given
        val accountDto = AccountDto(
            accountId = UUID.randomUUID(),
            username = UUID.randomUUID().toString(),
            email = "${nextInt(1, 10000)}@example.com"
        )
        val createdAccountDto = accountService.createAccount(accountDto)

        // when
        mockMvc.perform(
            get("/account/${createdAccountDto.accountId}")
                .header("X-User-Id", "baef6ba1-dc19-442e-a681-151c486190a4")
                .header("X-User-Role", "ADMIN")
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value(accountDto.username))
            .andExpect(jsonPath("$.email").value(accountDto.email))
    }

    @Test
    fun `update account should return 200`() {
        // given
        val accountDto = AccountDto(
            accountId = UUID.randomUUID(),
            username = UUID.randomUUID().toString(),
            email = "${nextInt(1, 10000)}@example.com"
        )
        val createdAccountDto = accountService.createAccount(accountDto)
        val updatedAccountDto =
            AccountDto(
                accountId = createdAccountDto.accountId,
                username = UUID.randomUUID().toString(),
                email = "updated@example.com"
            )

        // when
        mockMvc.perform(
            put("/account/${createdAccountDto.accountId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAccountDto))
                .header("X-User-Id", "baef6ba1-dc19-442e-a681-151c486190a4")
                .header("X-User-Role", "ADMIN")
        ).andExpect(status().isOk)

        // then
        jdbcTemplate.query(
            "SELECT username, email FROM account WHERE account_id = :accountId",
            mapOf("accountId" to createdAccountDto.accountId)
        ) { r, _ ->
            assertEquals(updatedAccountDto.username, r.getString("username"))
            assertEquals(updatedAccountDto.email, r.getString("email"))
        }
    }

}