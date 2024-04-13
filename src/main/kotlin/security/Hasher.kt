package security

import at.favre.lib.crypto.bcrypt.BCrypt
import java.util.*

class Hasher {
    companion object {
        private const val COST = 12;

        /**
         * Hashes a string using the BCrypt algorithm and base64 encodes the resulting ByteArray.
         *
         * @param password The password to be hashed.
         * @return A base64-encoded string representing the hashed password.
         */
        fun hash(password: String): String {
            val hashedPasswordBytes = BCrypt.withDefaults().hash(COST, password.toByteArray())
            return Base64.getEncoder().encodeToString(hashedPasswordBytes)
        }

        /**
         * Compares a user's input password with a hashed password
         * @param inputPassword The password entered by the user
         * @param hashedPassword The hashed password stored in the database
         * @return true if the passwords match, false otherwise
         */
        fun verify(inputPassword: String, hashedPassword: String): Boolean {
            val hashedPasswordBytes = Base64.getDecoder().decode(hashedPassword)
            return BCrypt.verifyer().verify(inputPassword.toByteArray(), hashedPasswordBytes).verified
        }
    }
}