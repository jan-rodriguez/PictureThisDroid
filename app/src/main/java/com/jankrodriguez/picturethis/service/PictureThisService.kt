package com.jankrodriguez.picturethis.service

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.jankrodriguez.picturethis.model.*
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

val RETROFIT_INSTANCE: Retrofit = Retrofit.Builder()
    .baseUrl("http://192.168.1.8:8080")
    .client(OkHttpClient.Builder()
        .addNetworkInterceptor(StethoInterceptor())
        .build())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

val PICTURE_THIS_SERVICE_INSTANCE: PictureThisService =
    RETROFIT_INSTANCE.create(PictureThisService::class.java)

interface PictureThisService {

  @POST("/user")
  fun createUser(@Body user: CreateUserBody): Single<User>

  @GET("/user/{user_id}/challenges")
  fun getReceivedChallenges(@Path("user_id") userId: String): Single<Array<Challenge>>

  @Multipart
  @POST("/image")
  fun uploadImage(@Part file: MultipartBody.Part): Single<UploadImageResponse>

  @POST("/challenge")
  fun createChallenge(@Body createChallengeBody: CreateChallengeBody): Single<Challenge>

}
