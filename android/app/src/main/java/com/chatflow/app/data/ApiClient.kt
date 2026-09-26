package com.chatflow.app.data

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL =
        "http://10.62.220.156:3000/"

    private val gson =
        GsonBuilder()
            .registerTypeAdapter(
                Message::class.java,
                MessageJsonDeserializer()
            )
            .create()

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .build()

    val authApi: AuthApi =
        retrofit.create(
            AuthApi::class.java
        )

    val messageAttachmentApi: MessageAttachmentApi =
        retrofit.create(
            MessageAttachmentApi::class.java
        )

    val userAvatarApi: UserAvatarApi =
        retrofit.create(
            UserAvatarApi::class.java
        )
}
