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

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import ru.stersh.apisonic.ApiSonicError
import ru.stersh.apisonic.models.ErrorResponse

internal class ErrorInterceptor(moshi: Moshi) : Interceptor {
    private val errorResponseAdapter = moshi.adapter(ErrorResponse::class.java)

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val body = response.body?.string() ?: return response
        tryThrowApiError(body)

        val newBody: ResponseBody = body.toResponseBody(response.body?.contentType())
        return response
            .newBuilder()
            .body(newBody)
            .build()
    }

    private fun tryThrowApiError(body: String) {
        try {
            val errorResponse = errorResponseAdapter.fromJson(body) ?: return
            throw ApiSonicError(errorResponse.data.error.code, errorResponse.data.error.message)
        } catch (exception: JsonDataException) {
            // Json body not valid or body not error
        } catch (exception: JsonEncodingException) {
            // Json body not valid or body not error
        }
    }
}
