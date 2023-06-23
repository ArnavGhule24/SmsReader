package com.example.apitest

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

//const val BASE_URL = "http://54.64.73.51:80/"

interface SmsService{

    @GET("categories")
    fun getCategories(@Query("email") email: String):Call<SmsCategories>

    @POST("postsms/signup")
    fun signupSms(@Query("email") email: String,@Body smsList:ArrayList<Sms>):Call<Sms>

    @POST("postsms/login")
    fun loginSms(@Query("email") email: String,@Body smsList:ArrayList<Sms>):Call<Sms>

    @POST("postsms/monthly")
    fun sendMonthlySms(@Query("email") email: String,@Body smsList:ArrayList<Sms>):Call<Sms>

}
