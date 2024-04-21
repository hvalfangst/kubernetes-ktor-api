package security

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import model.Access
import io.ktor.server.response.respond

class AccessControl {
    companion object {
        const val DELIMITER = "[SHADOW_SHAMAN_DUREK]"

        suspend fun validateAccess(call: ApplicationCall, requiredAccess: Access) {
            val user = call.authentication.principal<UserIdPrincipal>()

            if (user != null && !hasAccess(requiredAccess, Access.valueOf(user.name))) {
                call.respond(HttpStatusCode.Forbidden, "Access Denied")
            }
        }

        private fun hasAccess(requiredAccess: Access, userAccess: Access): Boolean {

            // ADMIN has access to CRUD
            if (userAccess == Access.ADMIN) {
                return true
            }

            // Define hierarchy with access inheritance for higher-level rights
            val accessRightsHierarchy = mapOf(
                Access.EDITOR to listOf(Access.EDITOR, Access.CREATOR, Access.VIEWER),
                Access.CREATOR to listOf(Access.CREATOR, Access.VIEWER),
                Access.VIEWER to listOf(Access.VIEWER)
            )

            // Check if the required access right is contained in the access hierarchy
            for (access in accessRightsHierarchy[userAccess] ?: emptyList()) {
                if (access == requiredAccess) {
                    return true
                }
            }

            // User does not have the required access right
            return false
        }
    }
}