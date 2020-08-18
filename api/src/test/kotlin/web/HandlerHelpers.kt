package web

import adapters.authentication.CookbookAccessManager
import adapters.authentication.HMAC512Provider

object HandlerHelpers {
    fun provideJWTProvider() = HMAC512Provider.provide("secret")

    fun provideAccessManager() = CookbookAccessManager(
        "roles", mapOf(
            "ANYONE" to CookbookRoles.ANYONE,
            "USER" to CookbookRoles.USER,
            "ADMIN" to CookbookRoles.ADMIN
        ), CookbookRoles.ANYONE
    )
}
