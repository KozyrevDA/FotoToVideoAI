package data.network.billing

import data.model.AuthResponse
import data.model.billing.google.PurchaseGoogle
import data.network.SpaceXApi
import data.network.billing.dto.RevCatPurchaseDTO
import data.network.user.tryRefresh
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

private const val PURCHASE_CONFIRM_GOOGLE = "purchase/google/confirm"

@Throws(Exception::class)
suspend fun SpaceXApi.confirmPurchaseGoogle(
    purchaseGoogle: PurchaseGoogle,
): Boolean? = try {
    suspend fun doConfirmPurchase(): HttpResponse? =
        httpClient.get(PURCHASE_CONFIRM_GOOGLE) {
            header(HttpHeaders.Authorization, "Bearer ${tokensManager.getAutTokens()?.accessToken}")
            parameter("token", purchaseGoogle.purchaseToken)
            parameter("product_id", purchaseGoogle.products.firstOrNull())
            parameter("package_name", "org.nla.phototovideoai")
            parameter("purchase_type", purchaseGoogle.purchaseGoogleType)
        }

    val response = doConfirmPurchase() ?: return null

    when (response.status) {
        HttpStatusCode.OK -> response.body<Boolean>()
        HttpStatusCode.BadRequest -> {
            println("doConfirmPurchase(), HttpStatusCode.BadRequest, ${response.status}")
            null
        }

        HttpStatusCode.InternalServerError -> {
            println("doConfirmPurchase(), HttpStatusCode.InternalServerError, ${response.status}")
            null
        }

        HttpStatusCode.Conflict -> {
            println("doConfirmPurchase(), HttpStatusCode.Conflict, ${response.status}")
            null
        }

        HttpStatusCode.Unauthorized -> {
            if (tryRefresh() is AuthResponse.Authorized) {
                val retryResponse = doConfirmPurchase() ?: return null
                if (retryResponse.status == HttpStatusCode.OK) {
                    retryResponse.body<Boolean>()
                } else {
                    null
                }
            } else {
                null
            }
        }

        else -> null
    }
} catch (e: Exception) {
    null
}

@Throws(Exception::class)
suspend fun SpaceXApi.confirmPurchaseRevCat(
    revCatPurchasesList: List<RevCatPurchaseDTO>,
): Boolean? = try {
    suspend fun doConfirmPurchase(): HttpResponse? =
        httpClient.post(PURCHASE_CONFIRM_GOOGLE) {
            header(HttpHeaders.Authorization, "Bearer ${tokensManager.getAutTokens()?.accessToken}")
            contentType(ContentType.Application.Json)
            setBody(revCatPurchasesList)
        }

    val response = doConfirmPurchase() ?: return null

    when (response.status) {
        HttpStatusCode.OK -> response.body<Boolean>()
        HttpStatusCode.BadRequest -> {
            println("doConfirmPurchase(), HttpStatusCode.BadRequest, ${response.status}")
            null
        }

        HttpStatusCode.InternalServerError -> {
            println("doConfirmPurchase(), HttpStatusCode.InternalServerError, ${response.status}")
            null
        }

        HttpStatusCode.Conflict -> {
            println("doConfirmPurchase(), HttpStatusCode.Conflict, ${response.status}")
            null
        }

        HttpStatusCode.Unauthorized -> {
            if (tryRefresh() is AuthResponse.Authorized) {
                val retryResponse = doConfirmPurchase() ?: return null
                if (retryResponse.status == HttpStatusCode.OK) {
                    retryResponse.body<Boolean>()
                } else {
                    null
                }
            } else {
                null
            }
        }

        else -> null
    }
} catch (e: Exception) {
    null
}