package org.censusmate.mobile.data.mapper

import org.censusmate.mobile.domain.model.Paged
import org.censusmate.mobile.domain.model.Person
import org.censusmate.mobile.data.remote.model.PersonResponseDto
import org.censusmate.mobile.data.remote.model.PersonsResponseDto

fun PersonResponseDto.toDomain() = Person(
    id = id,
    householdId = householdId,
    gender = gender,
    birthDate = birthDate,
    citizenship = citizenship,
    hasDualCitizenship = hasDualCitizenship,
    nationality = nationality,
    nativeLanguage = nativeLanguage,
    speaksRussian = speaksRussian,
    otherLanguages = otherLanguages,
    educationLevel = educationLevel,
    maritalStatus = maritalStatus,
    childrenCount = childrenCount,
    relationToHousehold = relationToHousehold,
    placeOfBirth = placeOfBirth,
    currentResidence = currentResidence,
    incomeSources = incomeSources,
    employmentStatus = employmentStatus,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun PersonsResponseDto.toDomain(): Paged<Person> = Paged(
    total = pagination.total,
    page = pagination.page,
    limit = pagination.limit,
    pages = pagination.pages,
    items = persons.map { it.toDomain() }
)