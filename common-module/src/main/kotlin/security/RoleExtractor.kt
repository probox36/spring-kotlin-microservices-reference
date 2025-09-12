    package com.buoyancy.common.security

    import io.github.oshai.kotlinlogging.KotlinLogging
    import org.springframework.context.annotation.Bean
    import org.springframework.context.annotation.Configuration
    import org.springframework.security.core.authority.SimpleGrantedAuthority
    import org.springframework.security.oauth2.jwt.Jwt
    import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter

    @Configuration
    class RoleExtractor {

        // A custom JwtAuthenticationConverter, which extracts Keycloak user roles from the JWT,
        // converts them to SimpleGrantedAuthority objects, and puts into SecurityContextHolder

        private val log = KotlinLogging.logger {}

        @Bean
        fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
            val grantedAuthoritiesConverter = { jwt: Jwt ->
                val realmAccess = jwt.claims["realm_access"] as? Map<*, *>
                val roles = realmAccess?.get("roles") as? List<String> ?: emptyList()
                log.info { "Extracted roles: $roles" }
                roles.map { SimpleGrantedAuthority("ROLE_${it.uppercase()}") }
            }

            val converter = JwtAuthenticationConverter()
            converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
            return converter
        }
    }