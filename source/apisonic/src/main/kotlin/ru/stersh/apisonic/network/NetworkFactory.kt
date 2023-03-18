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

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.stersh.apisonic.LogLevel

internal class NetworkFactory(
    authInterceptor: AuthenticationInterceptor,
    logLevel: LogLevel,
) {

    private val okHttpClient: OkHttpClient
    private val retrofitBuilder: Retrofit.Builder

    init {
        val loggingLevel = when (logLevel) {
            LogLevel.NONE -> HttpLoggingInterceptor.Level.NONE
            LogLevel.BASIC -> HttpLoggingInterceptor.Level.BASIC
            LogLevel.HEADERS -> HttpLoggingInterceptor.Level.HEADERS
            LogLevel.BODY -> HttpLoggingInterceptor.Level.BODY
        }
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(loggingLevel)

        val moshi = Moshi.Builder().build()
        val moshiConverterFactory = MoshiConverterFactory.create(moshi)

        val errorInterceptor = ErrorInterceptor(moshi)

        okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(errorInterceptor)
            .build()

        retrofitBuilder = Retrofit
            .Builder()
            .addConverterFactory(moshiConverterFactory)
            .client(okHttpClient)
    }

    fun <T> createApi(
        apiClass: Class<T>,
        baseUrl: String,
    ): T {
        return retrofitBuilder
            .baseUrl(baseUrl)
            .build()
            .create(apiClass)
    }
}
