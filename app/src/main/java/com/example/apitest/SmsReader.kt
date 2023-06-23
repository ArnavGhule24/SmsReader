package com.example.apitest

import android.content.ContentResolver
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SmsReader {

    var smsList = ArrayList<Sms>()
    var monthlySmsList = ArrayList<Sms>()


    fun readSms(contentResolver: ContentResolver,sharedPreferences: SharedPreferences):ArrayList<Sms> {

        //var inboxUri : Uri = Uri.parse("content://sms/inbox")

        val inboxUri: Uri = Telephony.Sms.Inbox.CONTENT_URI

        val sortOrder = "${Telephony.Sms._ID} DESC"

        var lastMessageId = 0
        var lastMessageDate =""

        //val selection = "${Telephony.Sms._ID} > $lastMessageId"

        val cursor: Cursor = contentResolver.query(inboxUri, null, null, null, sortOrder)!!

        val transactionDetails = TransactionDetails()

        var i=0
        while (cursor.moveToNext() && i!=100) {
            val smsSender=
                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)).toString()
            val smsBody=
                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)).toString()
            val date: String =
                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)).toString()
            val smsId=
                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms._ID)).toString()

            lastMessageId = smsId.toInt()

            val millisecond= date.toLong()

            val smsDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE).format(Date(millisecond)).toString()

            lastMessageDate = smsDate

            if (smsBody.contains("A/c no", true) || smsBody.contains("A/c", true)) {
                if (smsBody.contains("debit", true)
                    || smsBody.contains("debited", true)
                    || smsBody.contains("transferred", true)
                    || smsBody.contains("transfer", true)
                ) {

                    val amount = transactionDetails.getAmount(smsBody).toString()
                    var merchant = transactionDetails.getMerchant(smsBody,smsSender).toString()
                    if(merchant.equals("Dunzo Digital",true)){
                        merchant = "Dunzo"
                    }
                    smsList.add(Sms(merchant,amount,smsDate,smsId))
                    i++
                }
            }

        }

        cursor.close()

        sharedPreferences.edit().putInt("last_message_id", lastMessageId).apply()
        sharedPreferences.edit().putString("last_message_date", lastMessageDate).apply()
        return smsList

    }


    fun readNewSms(contentResolver: ContentResolver,sharedPreferences: SharedPreferences):ArrayList<Sms> {

        //var inboxUri : Uri = Uri.parse("content://sms/inbox")

        val inboxUri: Uri = Telephony.Sms.Inbox.CONTENT_URI

        var lastMessageId = sharedPreferences.getInt("last_message_id", 0)
        var lastMessageDate = sharedPreferences.getString("last_message","")

        val sortOrder = "${Telephony.Sms._ID} DESC"

        val selection = "${Telephony.Sms._ID} > $lastMessageId"


        val cursor: Cursor = contentResolver.query(inboxUri, null, selection, null, sortOrder)!!

        val transactionDetails = TransactionDetails()

        var i=0
        while (cursor.moveToNext() && i!=100) {
            val smsSender=
                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)).toString()
            val smsBody=
                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)).toString()
            val date: String =
                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)).toString()
            val smsId=
                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms._ID)).toString()

            lastMessageId = smsId.toInt()

            val millisecond= date.toLong()

            val smsDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE).format(Date(millisecond)).toString()

            lastMessageDate = smsDate

            if (smsBody.contains("A/c no", true) || smsBody.contains("A/c", true)) {
                if (smsBody.contains("debit", true)
                    || smsBody.contains("debited", true)
                    || smsBody.contains("transferred", true)
                    || smsBody.contains("transfer", true)
                ) {

                    val amount = transactionDetails.getAmount(smsBody).toString()
                    val merchant = transactionDetails.getMerchant(smsBody,smsSender).toString()
                    smsList.add(Sms(merchant,amount,smsDate,smsId))
                    i++
                }
            }


        }

        cursor.close()

        sharedPreferences.edit().putInt("last_message_id", lastMessageId).apply()
        return smsList

    }


    fun readMonthlySms(contentResolver: ContentResolver,sharedPreferences: SharedPreferences):ArrayList<Sms> {

        val inboxUri: Uri = Telephony.Sms.Inbox.CONTENT_URI

        var lastMessageId = sharedPreferences.getInt("last_message_id",0)
        var lastMessageDate = sharedPreferences.getString("last_message","").toString()

        val sortOrder = "${Telephony.Sms._ID} DESC"

        val selection = "${Telephony.Sms._ID} > $lastMessageId"

        val cursor: Cursor = contentResolver.query(inboxUri, null, selection, null, sortOrder)!!

        val df1: DateFormat = SimpleDateFormat("MM")
        val df2: DateFormat = SimpleDateFormat("yyyy")
        val date = Date()
        val month = df1.format(date).toString()
        val year = df2.format(date).toString()

//        if(month!=lastMessageDate.substring(3,5) || year != lastMessageDate.substring(6,10)){
//            val chatRef = firestore.collection("chat").document("messages")
//            chatRef.delete()
//                .addOnSuccessListener {
//                    // collection deleted successfully
//                }
//                .addOnFailureListener { exception ->
//                    // an error occurred while deleting the collection
//                    Log.w(TAG, "Error deleting collection: ", exception)
//                }
//
//        }


        val transactionDetails = TransactionDetails()

        while (cursor.moveToNext()) {
            val smsSender=
                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)).toString()
            val smsBody=
                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)).toString()
            val date: String =
                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)).toString()
            val smsId=
                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms._ID)).toString()

            lastMessageId = smsId.toInt()

            val millisecond= date.toLong()

            val smsDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE).format(Date(millisecond)).toString()


            if(smsDate.substring(3,5)==month && smsDate.substring(6,10)==year){
                if (smsBody.contains("A/c no", true) || smsBody.contains("A/c", true)) {
                    if (smsBody.contains("debit", true)
                        || smsBody.contains("debited", true)
                        || smsBody.contains("transferred", true)
                        || smsBody.contains("transfer", true)
                    ) {

                        val amount = transactionDetails.getAmount(smsBody).toString()
                        val merchant = transactionDetails.getMerchant(smsBody,smsSender).toString()
                        monthlySmsList.add(Sms(merchant,amount,smsDate,smsId))

                    }
                }
            }


        }
        cursor.close()

        sharedPreferences.edit().putInt("last_message_id",lastMessageId)

        return monthlySmsList
        //loadMonthlySms()

    }



}