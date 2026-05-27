package org.censusmate.mobile.domain.model

enum class Role {
    AGENT, ADMINISTRATOR;

    companion object {
        fun fromString(value: String): Role = when (value.lowercase()) {
            "administrator" -> ADMINISTRATOR
            else -> AGENT
        }
    }
}

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: Role,
    val isBlocked: Boolean,
    val createdAt: String,
    val updatedAt: String?
) {
    val fullName: String get() = "$firstName $lastName".trim()
    val isAdmin: Boolean get() = role == Role.ADMINISTRATOR
}