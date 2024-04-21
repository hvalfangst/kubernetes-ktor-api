package messages

import io.ktor.http.*

/**
 * Enum representing common error messages and their associated HTTP status codes.
 */
enum class ErrorMessages(val message: String, val httpStatusCode: HttpStatusCode) {

    // User related errors
    USER_ALREADY_EXISTS("A user associated with the given email address already exists in the system", HttpStatusCode.BadRequest),
    USER_NONEXISTENT("The requested user does not exist", HttpStatusCode.NotFound),
    USER_CREATION_FAILED("Failed to create user", HttpStatusCode.BadRequest),
    USER_UPDATE_FAILED("Failed to update user", HttpStatusCode.BadRequest),
    USER_DELETION_FAILED("Failed to delete user", HttpStatusCode.BadRequest),

    // Hero related errors
    HERO_ALREADY_EXISTS("A hero associated with the given name already exists in the system", HttpStatusCode.BadRequest),
    HERO_NONEXISTENT("The requested hero does not exist", HttpStatusCode.NotFound),
    HERO_CREATION_FAILED("Failed to create hero", HttpStatusCode.BadRequest),
    HERO_UPDATE_FAILED("Failed to update hero", HttpStatusCode.BadRequest),
    HERO_DELETION_FAILED("Failed to delete hero", HttpStatusCode.BadRequest),

    // Ability related errors
    ABILITY_ALREADY_EXISTS("An ability associated with the given name already exists in the system", HttpStatusCode.BadRequest),
    ABILITY_NONEXISTENT("The requested ability does not exist", HttpStatusCode.NotFound),
    ABILITY_CREATION_FAILED("Failed to create ability", HttpStatusCode.BadRequest),
    ABILITY_UPDATE_FAILED("Failed to update ability", HttpStatusCode.BadRequest),
    ABILITY_DELETION_FAILED("Failed to delete ability", HttpStatusCode.BadRequest),

    // Request body error
    REQUEST_BODY_VALIDATION_FAILURE("Validation of request body failed", HttpStatusCode.BadRequest),

    // Authentication and authorization related errors
    AUTH_INVALID_HEADER("Missing or malformed authorization header detected", HttpStatusCode.Unauthorized),
    AUTH_MISSING_USER("Failed to identify any users matching mail associated with 'username' in Basic header", HttpStatusCode.Unauthorized),
    AUTH_PASSWORD_MISMATCH("Password mismatch between header content and database detected", HttpStatusCode.Unauthorized);
}