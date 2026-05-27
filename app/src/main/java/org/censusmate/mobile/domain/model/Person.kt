package org.censusmate.mobile.domain.model

data class Person(
    val id: String,
    val householdId: String,
    val gender: String?,
    val birthDate: String,
    val citizenship: String?,
    val hasDualCitizenship: Boolean?,
    val nationality: String?,
    val nativeLanguage: String?,
    val speaksRussian: Boolean?,
    val otherLanguages: List<String>,
    val educationLevel: String?,
    val maritalStatus: String?,
    val childrenCount: Int?,
    val relationToHousehold: String?,
    val placeOfBirth: String?,
    val currentResidence: String?,
    val incomeSources: List<String>,
    val employmentStatus: String?,
    val createdAt: String,
    val updatedAt: String?
)