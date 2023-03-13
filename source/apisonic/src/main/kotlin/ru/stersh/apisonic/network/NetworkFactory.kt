package ru.stersh.apisonic.network

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.stersh.apisonic.LogLevel

internal class NetworkFactory(
    authInterceptor: AuthenticationInterceptor,
    logLevel: LogLevel
) {

    private val okHttpClient: OkHttpClient
    private val retrofitBuilder: Retrofit.Builder

    init {
        val loggingLevel = when(logLevel) {
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
        baseUrl: String
    ): T {
        return retrofitBuilder
            .baseUrl(baseUrl)
            .build()
            .create(apiClass)
    }
}