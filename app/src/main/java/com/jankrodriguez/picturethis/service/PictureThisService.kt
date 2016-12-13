package com.jankrodriguez.picturethis.service

import com.jankrodriguez.picturethis.model.Challenge
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface PictureThisService {

    @GET("/user/{user_id}/challenges")
    fun getChallenges(@Path("user_id") userId: String): Observable<Array<Challenge>>

}
