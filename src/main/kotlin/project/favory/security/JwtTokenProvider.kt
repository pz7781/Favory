package project.favory.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtTokenProvider (

    @Value("\${jwt.secret}") secret: String,

    // 보안상 - 1시간
    @Value("\${jwt.access-token-expiration-ms}")
    private val accessTokenValidity: Long,

    // 새로 발급 - 14일
    @Value("\${jwt.refresh-token-expiration-ms}")
    private val refreshTokenValidity: Long
    ) {

        private val key = Keys.hmacShaKeyFor( secret.toByteArray())

        fun generateAccessToken(userId: Long, email: String): String {
            val now = Date()
            val expiry = Date(now.time + accessTokenValidity)

            return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .claim("uid", userId)
                .claim("typ", "access")
                .signWith(key)
                .compact()
        }

        fun generateRefreshToken(userId: Long, email: String): String {
            val now = Date()
            val expiry = Date(now.time + refreshTokenValidity)

            return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .claim("uid", userId)
                .claim("typ", "refresh")
                .signWith(key)
                .compact()
        }

        fun validateToken(token: String): Boolean = try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            true
        } catch (ex: Exception) {
            false
        }

        fun getAuthentication(token: String): UsernamePasswordAuthenticationToken {
            val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
            val email = claims.subject
            val userId = (claims["uid"] as Number).toLong()

            val principal = User(email, "", listOf(SimpleGrantedAuthority("ROLE_USER")))
            val auth = UsernamePasswordAuthenticationToken(principal, token, principal.authorities)
            auth.details = userId
            return auth
        }

        fun isAccessToken(token: String) =
            runCatching {
                val c = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
                c["typ"] == "access"
            }.getOrDefault(false)

        fun isRefreshToken(token: String) =
            runCatching {
                val c = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
                c["typ"] == "refresh"
            }.getOrDefault(false)

        fun getUserId(token: String): Long {
            val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
            return (claims["uid"] as Number).toLong()
        }

        fun getEmail(token: String): String {
            val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
            return claims.subject
        }

        fun getRefreshTokenExpiryMillis(): Long = refreshTokenValidity
    }