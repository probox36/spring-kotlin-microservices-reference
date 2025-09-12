package com.buoyancy.common.security

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SecurityContextLoggingFilter : OncePerRequestFilter() {

    private val log = KotlinLogging.logger {}

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth != null) {
            log.info { "Authentication object in SecurityContextHolder:" }
            log.info { "  Principal: ${(auth.principal as Jwt).subject}" }
            log.info { "  Authorities: ${auth.authorities}" }
            log.info { "  Details: ${auth.details}" }
            log.info { "  Authenticated: ${auth.isAuthenticated}" }
        }

        filterChain.doFilter(request, response)
    }
}