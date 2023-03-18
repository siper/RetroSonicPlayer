/*
 * Copyright (c) 2020 Retro Sonic contributors.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package ru.stersh.apisonic.network

import okhttp3.Interceptor
import okhttp3.Response
import ru.stersh.apisonic.Security

internal class AuthenticationInterceptor(
    private val username: String,
    private val password: String,
    private val apiVersion: String,
    private val clientId: String,
    private val useLegacyAuth: Boolean,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val urlBuilder = request.url.newBuilder()
            .addQueryParameter("u", username)
            .addQueryParameter("v", apiVersion)
            .addQueryParameter("c", clientId)
            .addQueryParameter("f", "json")
        if (useLegacyAuth) {
            urlBuilder.addQueryParameter("p", password)
        } else {
            val salt = Security.generateSalt()
            val token = Security.getToken(salt, password)
            urlBuilder
                .addQueryParameter("s", salt)
                .addQueryParameter("t", token)
        }
        val url = urlBuilder.build()
        return chain.proceed(request.newBuilder().url(url).build())
    }
}
