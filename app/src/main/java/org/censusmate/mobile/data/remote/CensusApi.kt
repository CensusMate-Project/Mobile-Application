package org.censusmate.mobile.data.remote

import io.ktor.client.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.censusmate.api.AddressApi
import org.censusmate.api.AuthApi
import org.censusmate.api.EventsApi
import org.censusmate.api.HouseholdsApi
import org.censusmate.api.PersonsApi
import org.censusmate.api.StatsApi
import org.censusmate.api.UsersApi
import org.censusmate.mobile.data.local.TokenDataStore

class CensusApi(tokenDataStore: TokenDataStore) {
    @OptIn(ExperimentalSerializationApi::class)
    private val config: HttpClientConfig<*>.() -> Unit = {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                namingStrategy = JsonNamingStrategy.SnakeCase
            })
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val token = tokenDataStore.get()
                    if (token != null) BearerTokens(token, "") else null
                }
            }
        }
        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.BODY
        }
    }

    private val baseUrl = "http://10.0.2.2:3000"

    val auth by lazy { AuthApi(baseUrl, httpClientConfig = config) }
    val users by lazy { UsersApi(baseUrl, httpClientConfig = config) }
    val events by lazy { EventsApi(baseUrl, httpClientConfig = config) }
    val households by lazy { HouseholdsApi(baseUrl, httpClientConfig = config) }
    val persons by lazy { PersonsApi(baseUrl, httpClientConfig = config) }
    val stats by lazy { StatsApi(baseUrl, httpClientConfig = config) }
    val address by lazy { AddressApi(baseUrl, httpClientConfig = config) }
}