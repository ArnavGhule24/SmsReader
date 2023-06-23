package com.example.apitest

import java.util.regex.Matcher
import java.util.regex.Pattern

class TransactionDetails {

    fun getAmount(body : String): String? {

        // pattern - rs. **,***.**
        val pattern1 = "(?i)(?:(?:RS|INR|MRP)\\.?\\s?)(\\d+(:?\\,\\d+)?(\\,\\d+)?(\\.\\d{1,2})?)"
        val regex1: Pattern = Pattern.compile(pattern1)
        // pattern - inr **,***.**
        val pattern2 = "(?i)(?:(?:RS|INR|MRP)\\.?\\s?)(\\d+(:?\\,\\d+)?(\\,\\d+)?(\\.\\d{1,2})?)"
        val regex2: Pattern = Pattern.compile(pattern2)
        val matcher1: Matcher = regex1.matcher(body)
        val matcher2: Matcher = regex2.matcher(body)
        if (matcher1.find()) {
            try {
                var a: String = matcher1.group(1)
                return a

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (matcher2.find()) {
            try {

                var a: String = matcher2.group(1)
                return a
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return null
    }


    fun getMerchant(body: String,sender:String):String?{

        if(sender.contains("AxisBk",true)){
            val pattern1 = "(?i)(?:\\d{12}\\/)([A-Za-z0-9]*\\s?-?\\s?[A-Za-z0-9]*\\s?-?\\.?)"
            val regex1: Pattern = Pattern.compile(pattern1)

            val pattern2 = "(?i)(?:PUR\\/)([A-Za-z0-9]*\\s?-?\\s?[A-Za-z0-9]*\\s?-?\\.?)"
            val regex2: Pattern = Pattern.compile(pattern2)

            val matcher1: Matcher = regex1.matcher(body)
            val matcher2: Matcher = regex2.matcher(body)
            if (matcher1.find()) {
                try {
                    var a: String = matcher1.group(1)
                    return a

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (matcher2.find()) {
                try {
                    var a: String = matcher2.group(1)
                    return a
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        else if(sender.contains("SBI",true)){
            val pattern1 = "(?i)(?:\\sto\\s)([A-Za-z0-9]*\\s?-?\\s?[A-Za-z0-9]*\\s?-?\\s?[A-Za-z0-9]*\\s?-?\\.?)(?:\\sRef\\s)"
            val regex1: Pattern = Pattern.compile(pattern1)
            val matcher1: Matcher = regex1.matcher(body)
            if (matcher1.find()) {
                try {
                    var a: String = matcher1.group(1)
                    return a

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return null
    }

}