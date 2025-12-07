package project.favory.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    // 토큰 검사 제외 경로
    private val whiteListPrefixes = listOf(
        "/login", "/signup",
        "/swagger-ui", "/v3/api-docs", "/error"
    )

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return whiteListPrefixes.any { prefix -> path.startsWith(prefix) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        val token = header?.takeIf { it.startsWith("Bearer ") }?.substring(7)

        if (!token.isNullOrBlank()
            && jwtTokenProvider.validateToken(token)
            && jwtTokenProvider.isAccessToken(token)
        ) {
            val auth = jwtTokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = auth
        }

        chain.doFilter(request, response)
    }

}