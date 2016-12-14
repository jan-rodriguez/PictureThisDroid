package com.jankrodriguez.picturethis.service

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.jankrodriguez.picturethis.model.Challenge
import com.jankrodriguez.picturethis.model.CreateUserBody
import com.jankrodriguez.picturethis.model.User
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

val RETROFIT_INSTANCE: Retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:8080")
    .client(OkHttpClient.Builder()
        .addNetworkInterceptor(StethoInterceptor())
        .build())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val PICTURE_THIS_SERVICE_INSTANCE: PictureThisService =
    RETROFIT_INSTANCE.create(PictureThisService::class.java)

interface PictureThisService {

    @POST("/user")
    fun createUser(@Body user: CreateUserBody): Observable<User>

    @GET("/user/{user_id}/challenges")
    fun getReceivedChallenges(@Path("user_id") userId: String): Observable<Array<Challenge>>

}
