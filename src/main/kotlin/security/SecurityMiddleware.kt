package security

import io.ktor.server.application.*
import io.ktor.server.auth.*
import service.CustomerService


class SecurityMiddleware {

    fun configure(application: Application, customerService: CustomerService) {

        // Install the Authentication feature and configure authentication methods
        application.install(Authentication) {

            // Configure basic authentication
            basic("auth-basic") {
                validate { credentials ->
                    val (username, password) = credentials
                    val user = customerService.getCustomerByEmail(username)
                    if (user != null && Hasher.verify(password, user.password)) {
                        // Return the concatenation of username and access if authentication is successful
                        UserIdPrincipal("$username${AccessControl.DELIMITER}${user.access}")
                    } else {
                        null
                    }
                }
            }

            // Configure JWT bearer authentication
            bearer("auth-jwt") {
                authenticate { credential ->
                    val decodedJWT = JwtUtil.verifyToken(credential.token)
                    val usernameClaim = decodedJWT.getClaim("username")
                    val accessClaim = decodedJWT.getClaim("access")

                    if (!usernameClaim.isNull && !accessClaim.isNull) {
                        // Return the access on success
                        val access = accessClaim.asString()
                        UserIdPrincipal(access)
                    } else {
                        null
                    }
                }
            }
        }
    }
}