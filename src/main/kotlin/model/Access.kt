package model

enum class Access(val toString: String) {
    VIEWER( "VIEWER"),
    CREATOR("CREATOR"),
    EDITOR("EDITOR"),
    ADMIN("ADMIN")
}