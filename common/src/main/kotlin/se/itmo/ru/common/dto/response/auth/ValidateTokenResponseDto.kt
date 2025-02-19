package se.itmo.ru.common.dto.response.auth

import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.annotations.NotNull
import java.util.UUID
import javax.xml.crypto.Data


data class ValidateTokenResponseDto(
        @NotNull
        @JsonProperty("user_id")
        val userId: UUID? = null,

        @NotNull
        @JsonProperty("role")
        val userRoleDto: String? = null
)