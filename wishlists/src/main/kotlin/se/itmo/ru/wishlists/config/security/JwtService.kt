package se.itmo.ru.wishlists.config.security


import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Key

@Service
class JwtService {

    @Value("\${jwt.secret}")
    private val secretKey: String? = null

    private fun getSignKey(): Key {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun isValid(token: String): Boolean {
        try {
            extract(token)
            return true;
        } catch (e: Throwable) {
            return false;
        }
    }

    fun extract(token: String): Claims {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .body
    }
}