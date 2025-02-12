package se.itmo.ru.authservice.models

import com.fasterxml.jackson.databind.annotation.EnumNaming
import jakarta.ws.rs.DefaultValue
import org.jetbrains.annotations.NotNull
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table(name = "users")
data class User(
        @field:Id
        @field:Column("id")
        var id: UUID,

        @field:Column("login")
        @field:NotNull
        var login: String,

        @field:Column("password")
        @field:NotNull
        var password: String,

        @field:Column("role")
        var role: UserRole = UserRole.USER,

        @field:Column("created_at")
        @field:NotNull
        var createdAt: LocalDateTime = LocalDateTime.now(),

        @field:Column("updated_at")
        @field:NotNull
        var updatedAt: LocalDateTime = LocalDateTime.now(),
)