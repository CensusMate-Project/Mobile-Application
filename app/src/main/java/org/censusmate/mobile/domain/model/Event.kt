package org.censusmate.mobile.domain.model

data class Event(
    val id: String,
    val name: String,
    val startDatetime: String,
    val endDatetime: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String?
)