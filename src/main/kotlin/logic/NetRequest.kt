package com.lyneon.cytoidinfo.logic

import com.lyneon.cytoidinfo.model.B30Records
import com.lyneon.cytoidinfo.model.PlayerProfile
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

private val json = Json { ignoreUnknownKeys = true }

object NetRequest {
    fun getPlayerProfile(playerName: String): PlayerProfile {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://services.cytoid.io/profile/$playerName/details")
            .build()
        val response = client.newCall(request).execute()
        val result = response.body?.string()
        if (result == null) {
            throw Exception("Request failed")
        } else {
            return json.decodeFromString(result)
        }
    }
    
    fun getB30Records(playerName: String, count: Int = 30): B30Records {
        val client = OkHttpClient()
//        val data = "{\"query\":\"query StudioAnalytics(${'$'}id:ID=\"${id}\") {profile(id:${'$'}id) {bestRecords(limit: 30) {score,details{perfect,great,good,bad,miss,maxCombo},mods,chart{name,difficulty,notesCount,level {title}},accuracy,rating}}}\"}"
        val data = """{"operationName":null,
            |"variables":{},
            |"query":"{profile(uid:\"$playerName\"){bestRecords(limit:$count){score,mods,accuracy,rating,details{perfect,great,good,bad,miss,maxCombo},chart{type,difficulty,notesCount,level{title,bundle{backgroundImage{original}}}}}}}"}""".trimMargin()
        val body = data.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://services.cytoid.io/graphql")
            .post(body)
            .build()
        val response = client.newCall(request).execute()
        val result = response.body?.string()
        if (result == null) {
            throw Exception("Request failed")
        } else {
            return json.decodeFromString(result)
        }
    }
}