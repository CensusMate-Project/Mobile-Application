package org.censusmate.mobile.domain.model

data class AuthUser(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: Role
) {
    val fullName: String get() = "$firstName $lastName".trim()
    val isAdmin: Boolean get() = role == Role.ADMINISTRATOR
}