package com.example.apitest

import android.Manifest
import android.content.ContentResolver
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var smsList = ArrayList<Sms>()
    var monthlySmsList = ArrayList<Sms>()
    lateinit var button: Button

    val smsReader = SmsReader()

    lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //getSms()

        button = findViewById(R.id.buttonGet)

        val contentResolver: ContentResolver = contentResolver

        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)

        var permissionCheck:Int =
            ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_SMS)

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            smsList = smsReader.readNewSms(contentResolver,sharedPreferences)
            monthlySmsList = smsReader.readMonthlySms(contentResolver,sharedPreferences)
            postSms()
            postMonthlySms()
        }
        else{
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_SMS),
                100
            )
        }

        button.setOnClickListener {

            categories()
        }

        //loadSms()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==100){
            val contentResolver: ContentResolver = contentResolver
            smsList = smsReader.readSms(contentResolver,sharedPreferences)
            monthlySmsList = smsReader.readMonthlySms(contentResolver,sharedPreferences)
            postSms()
            postMonthlySms()
        }
        else{
            Toast.makeText(this,"Until you give permission we cannot display messages", Toast.LENGTH_LONG).show()
        }
    }

    private fun postSms(){


        val smsService = ServiceBuilder.buildService(SmsService::class.java)


        val requestCall = smsService.signupSms("sanji",smsList)
        requestCall.enqueue(object :Callback<Sms>{
            override fun onResponse(call: Call<Sms>, response: Response<Sms>) {
                if(response.isSuccessful){
                    Log.d("POST","Successfully added")
                }
                else{
                    Log.d("POST","Failed to send smsList")
                }
            }

            override fun onFailure(call: Call<Sms>, t: Throwable) {
                Log.d("POST","Error occured: "+t.toString())
            }
        })



    }

    private fun postMonthlySms(){

        val smsService = ServiceBuilder.buildService(SmsService::class.java)
        val requestCall = smsService.sendMonthlySms("sanji",monthlySmsList)
        requestCall.enqueue(object :Callback<Sms>{
            override fun onResponse(call: Call<Sms>, response: Response<Sms>) {
                if(response.isSuccessful){
                    Log.d("POST","Successfully added")
                }
                else{
                    Log.d("POST","Failed to send smsList")
                }
            }

            override fun onFailure(call: Call<Sms>, t: Throwable) {
                Log.d("POST","Error occured: "+t.toString())
            }
        })

    }

    private fun categories(){

        val smsService = ServiceBuilder.buildService(SmsService::class.java)

        val requestCall = smsService.getCategories("sanji")
        requestCall.enqueue(object : Callback<SmsCategories>{
            override fun onResponse(call: Call<SmsCategories>, response: Response<SmsCategories>) {
                if(response.isSuccessful){
                    val smsCategories = response.body()!!
                    Log.d("GET",smsCategories.toString())
                }
                else if(response.code()==401){
                    Log.d("GET","Session has expired")
                }
                else{
                    Log.d("GET","Failed to retrieve items")
                }

            }

            override fun onFailure(call: Call<SmsCategories>, t: Throwable) {
                Log.d("GET","Error occured: "+t.toString())
            }

        })

    }

}