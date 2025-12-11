package project.favory.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.env.Environment
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import project.favory.repository.UserRepository

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
    private val environment: Environment
) : OncePerRequestFilter() {

    private val isProductionMode: Boolean
        get() = environment.activeProfiles.contains("prod")

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
        if (!isProductionMode) {
            val userIdHeader = request.getHeader("X-User-Id")
            if (!userIdHeader.isNullOrBlank()) {
                val userId = userIdHeader.toLongOrNull()
                if (userId != null) {
                    val user = userRepository.findByIdOrNull(userId)
                    if (user != null) {
                        val principal = User(user.email, "", listOf(SimpleGrantedAuthority("ROLE_USER")))
                        val auth = UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
                        auth.details = userId
                        SecurityContextHolder.getContext().authentication = auth
                        chain.doFilter(request, response)
                        return
                    }
                }
            }
        }

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