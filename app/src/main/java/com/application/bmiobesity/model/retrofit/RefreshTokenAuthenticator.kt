package com.application.bmiobesity.model.retrofit

import okhttp3.*

class RefreshTokenAuthenticator : Authenticator, Interceptor {
    override fun authenticate(route: Route?, response: Response): Request? {
        val i = 0
        return null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val i = 0
        val response = chain.proceed(chain.request())

        return response
    }
}