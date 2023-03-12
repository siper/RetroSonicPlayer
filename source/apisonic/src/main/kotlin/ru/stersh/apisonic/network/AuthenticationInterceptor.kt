package ru.stersh.apisonic.network

import okhttp3.Interceptor
import okhttp3.Response
import ru.stersh.apisonic.Security

internal class AuthenticationInterceptor(
    private val username: String,
    private val password: String,
    private val apiVersion: String,
    private val clientId: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val salt = Security.generateSalt()
        val token = Security.getToken(salt, password)
        val url = request.url.newBuilder()
            .addQueryParameter("u", username)
            .addQueryParameter("s", salt)
            .addQueryParameter("t", token)
            .addQueryParameter("v", apiVersion)
            .addQueryParameter("c", clientId)
            .addQueryParameter("f", "json")
            .build()
        return chain.proceed(request.newBuilder().url(url).build())
    }
}