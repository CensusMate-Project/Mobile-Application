package org.censusmate.mobile.domain.model

data class Household(
    val id: String,
    val enumeratorId: String?,
    val eventId: String?,
    val address: String,
    val totalResidents: Int,
    val dwellingType: String?,
    val buildingYear: String?,
    val totalArea: Int?,
    val livingArea: Int?,
    val roomsCount: Int?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String?
)