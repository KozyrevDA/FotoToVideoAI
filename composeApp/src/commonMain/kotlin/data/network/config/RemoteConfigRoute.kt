package data.network.config

import data.network.SpaceXApi
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get

private const val PRICES_VK_DONUT = "v2/remote-config/prices/vk-donut"
private const val SHOW_ONBOARDING = "v2/remote-config/show-onboarding"
private const val SHOW_START_PAYWALL = "v2/remote-config/show-start-paywall"
private const val MONTH_SUB_COINS = "v2/remote-config/month-sub-coins"
private const val SHOW_MORE_TOKENS_BUTTON_WHEN_NON_SUB =
    "v2/remote-config/show-more-tokens-button-when-non-sub"
private const val START_COINS = "v2/remote-config/start-coins"

@Throws(Exception::class)
suspend fun SpaceXApi.getPriceVkDonutMonthRub(): Int? = try {
    httpClient.get(PRICES_VK_DONUT).body<Int>()
} catch (_: Exception) {
    null
}

@Throws(Exception::class)
suspend fun SpaceXApi.getShowOnboarding(): Boolean = try {
    httpClient.get(SHOW_ONBOARDING) {
        timeout {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 3000
            socketTimeoutMillis = 8000
        }
    }.body()
} catch (_: Exception) {
    true
}

@Throws(Exception::class)
suspend fun SpaceXApi.getShowStartPaywall(): Boolean = try {
    httpClient.get(SHOW_START_PAYWALL) {
        timeout {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 3000
            socketTimeoutMillis = 8000
        }
    }.body<Boolean>()
} catch (_: Exception) {
    true
}

@Throws(Exception::class)
suspend fun SpaceXApi.getMonthSubCoins(): Int? = try {
    httpClient.get(MONTH_SUB_COINS) {
        timeout {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 3000
            socketTimeoutMillis = 8000
        }
    }.body<Int?>()
} catch (_: Exception) {
    null
}

@Throws(Exception::class)
suspend fun SpaceXApi.getShowMoreTokensButtonWhenNonSub(): Boolean = try {
    httpClient.get(SHOW_MORE_TOKENS_BUTTON_WHEN_NON_SUB) {
        timeout {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 3000
            socketTimeoutMillis = 8000
        }
    }.body<Boolean>()
} catch (_: Exception) {
    true
}

@Throws(Exception::class)
suspend fun SpaceXApi.getStartCoins(): Int = try {
    httpClient.get(START_COINS) {
        timeout {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 3000
            socketTimeoutMillis = 8000
        }
    }.body<Int>()
} catch (_: Exception) {
    0
}

private const val SHOW_TRIAL_GENERATION = "v2/remote-config/show-trial-generation"

@Throws(Exception::class)
suspend fun SpaceXApi.getShowTrialGeneration(): Boolean = try {
    httpClient.get(SHOW_TRIAL_GENERATION) {
        timeout {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 3000
            socketTimeoutMillis = 8000
        }
    }.body<Boolean>()
} catch (_: Exception) {
    false
}