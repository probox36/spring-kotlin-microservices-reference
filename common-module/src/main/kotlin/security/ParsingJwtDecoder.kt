package com.buoyancy.common.security

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Component
import java.text.ParseException

@Component
class ParsingJwtDecoder : JwtDecoder {

    // Stock decoder replacement, which just converts a token
    // into a Jwt object without any validation

    private val log = KotlinLogging.logger {}

    override fun decode(token: String): Jwt {
        try {
            val signedJwt = SignedJWT.parse(token)
            val claimsSet: JWTClaimsSet = signedJwt.jwtClaimsSet

            val headers = signedJwt.header.toJSONObject()
            val claims = claimsSet.claims
            log.info { "Extracted claims: $claims" }
            val expiresAt = claimsSet.expirationTime?.toInstant()
            val issuedAt = claimsSet.issueTime?.toInstant()

            return Jwt(
                token,
                issuedAt,
                expiresAt,
                headers,
                claims
            )
        } catch (e: ParseException) {
            throw BadJwtException("Failed to decode JWT: ${e.message}", e)
        }
    }
}