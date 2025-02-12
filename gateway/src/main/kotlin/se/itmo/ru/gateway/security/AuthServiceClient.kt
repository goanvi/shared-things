package se.itmo.ru.gateway.security

import jakarta.validation.Valid
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import se.itmo.ru.common.dto.request.auth.ValidateTokenRequestDto
import se.itmo.ru.common.dto.response.auth.ValidateTokenResponseDto

@FeignClient(name = "authentication-service", path = "/api/v1/auth")
interface AuthServiceClient {
    @PostMapping(value = ["/validate"], produces = ["application/json"], consumes = ["application/json"])
    fun validate(
            @Valid @RequestBody validateTokenRequestDto: ValidateTokenRequestDto?
    ): ValidateTokenResponseDto?
}