package com.coderfeng.tooyue.data.http

import android.util.Log
import com.coderfeng.tooyue.BuildConfig
import com.coderfeng.tooyue.user.api.UserApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.github.com/"
private const val TIME_OUT = 60L
private const val TAG = "fmt"

val httpLoggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        Log.e(TAG, message)
    }
}).also {
    it.level = HttpLoggingInterceptor.Level.BODY
}

val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
    .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
    .readTimeout(TIME_OUT, TimeUnit.SECONDS).also {
        if (BuildConfig.DEBUG) {
            it.addInterceptor(httpLoggingInterceptor)
        }
    }.build()

val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .baseUrl(BASE_URL)
    .build()


object UserService : UserApi by retrofit.create(UserApi::class.java)


