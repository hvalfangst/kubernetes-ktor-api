package security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import util.ConfigHandler
import java.util.*

object JwtUtil {
    private val jwtConfig = ConfigHandler.getJwtConfig()

    private val jwtVerifier: JWTVerifier = JWT.require(Algorithm.HMAC256(jwtConfig.secret))
        .withIssuer(jwtConfig.issuer)
        .build()

    fun generateToken(username: String, access: String): String {
        return JWT.create()
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.issuer)
            .withClaim("username", username)
            .withClaim("access", access)
            .withExpiresAt(Date(System.currentTimeMillis() + (60000 * 5))) // 5 Minutes
            .sign(Algorithm.HMAC256(jwtConfig.secret))
    }

    @Throws(JWTVerificationException::class)
    fun verifyToken(token: String): DecodedJWT {
        return jwtVerifier.verify(token)
    }
}