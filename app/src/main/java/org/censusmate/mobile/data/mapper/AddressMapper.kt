package org.censusmate.mobile.data.mapper

import org.censusmate.mobile.domain.model.Address
import org.censusmate.mobile.data.remote.model.AddressSuggestionDto

fun AddressSuggestionDto.toDomain() = Address(
    value = value,
    unrestrictedValue = unrestrictedValue
)