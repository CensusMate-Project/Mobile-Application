package org.censusmate.mobile.domain.model

data class Paged<T>(
    val total: Int,
    val page: Int,
    val limit: Int,
    val pages: Int,
    val items: List<T>
)