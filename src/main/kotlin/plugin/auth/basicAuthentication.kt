package plugin.auth

import io.ktor.server.auth.*

fun AuthenticationConfig.basicAuthentication() {
    basic("auth-basic") {
        realm = "Access to the '/' path"
        validate { credentials ->
            val username: String? = System.getenv("USERNAME")
            val password: String? = System.getenv("PASSWORD")

            if (credentials.name == username && credentials.password == password) {
                UserIdPrincipal(credentials.name)
            } else {
                null
            }
        }
    }
}