package se.itmo.ru.bookings.rest.client

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono
import se.itmo.ru.common.dto.AccountDto
import java.util.*

@ReactiveFeignClient(name = "accounts", path = "api/account")
interface AccountRestClient {

    @GetMapping(value = ["/{id}"], produces = ["application/json"])
    fun getAccountById(@PathVariable("id") accountId: UUID): Mono<AccountDto>
}