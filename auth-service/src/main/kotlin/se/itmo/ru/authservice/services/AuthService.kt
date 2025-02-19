package se.itmo.ru.authservice.services

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.InternalServerErrorException
import jakarta.ws.rs.NotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import se.itmo.ru.authservice.models.User
import java.security.Key
import java.util.*


@Service
class AuthService(
        private val userService: UserService
) {
    @Value("\${jwt.secret}")
    private val secretKey: String? = null


    fun validateAndExtractUser(token: String): Mono<User> {
        try {
            val claims = Jwts
                    .parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJwt(token)
                    .body

            val userId = claims.get("id", UUID::class.java)

            return userService
                    .findById(userId).switchIfEmpty(
                            Mono.error(NotFoundException())
                    )

        } catch (e: JwtException) {
            throw BadRequestException()
        } catch (e: Exception) {
            throw InternalServerErrorException()
        }

    }

    fun login() {}
    fun generateToken(user: User): String {
        val claims: MutableMap<String, Any> = HashMap()
        claims["user_id"] = user.id
        claims["username"] = user.username
        claims["role"] = user.role
        return createToken(claims, user.username)
    }

    private fun createToken(claims: Map<String, Any?>, userName: String): String {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(Date(System.currentTimeMillis()))
                .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact()
    }

    private fun getSignKey(): Key {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}