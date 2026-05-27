package org.censusmate.mobile.data.mapper

import org.censusmate.mobile.domain.model.LanguageCount
import org.censusmate.mobile.domain.model.Stats
import org.censusmate.mobile.data.remote.model.StatsResponseDto

fun StatsResponseDto.toDomain() = Stats(
    eventId = eventId,
    eventName = eventName,
    totalPopulation = totalPopulation,
    totalHouseholds = totalHouseholds,
    avgPersonsPerHousehold = avgPersonsPerHousehold,
    genderDistribution = genderDistribution,
    averageAge = averageAge,
    childrenCount = childrenCount,
    elderlyCount = elderlyCount,
    maritalStatusDistribution = maritalStatusDistribution,
    avgChildrenCount = avgChildrenCount,
    educationDistribution = educationDistribution,
    employmentDistribution = employmentDistribution,
    incomeSourcesDistribution = incomeSourcesDistribution,
    percentSpeaksRussian = percentSpeaksRussian,
    dualCitizenshipCount = dualCitizenshipCount,
    topOtherLanguages = topOtherLanguages.map { LanguageCount(it.language, it.count) },
    dwellingTypeDistribution = dwellingTypeDistribution,
    avgTotalArea = avgTotalArea,
    avgLivingArea = avgLivingArea
)