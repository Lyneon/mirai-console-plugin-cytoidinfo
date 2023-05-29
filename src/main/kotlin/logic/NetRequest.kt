package com.lyneon.cytoidinfo.logic

import com.lyneon.cytoidinfo.model.B30Records
import com.lyneon.cytoidinfo.model.PlayerProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

private val json = Json { ignoreUnknownKeys = true }

object NetRequest {
    suspend fun getPlayerProfile(playerName: String): PlayerProfile {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://services.cytoid.io/profile/$playerName/details")
            .build()
        val result = withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            when (response.code) {
                200 -> response.body?.string()
                404 -> throw Exception("未找到玩家")
                else -> throw Exception("Unknown Exception:HTTP response code ${response.code}")
            }
        }
        if (result == null) {
            throw Exception("Request failed")
        } else {
            return json.decodeFromString(result)
        }
    }
    
    suspend fun getB30Records(playerName: String, count: Int): B30Records {
        val client = OkHttpClient()
//        val data = "{\"query\":\"query StudioAnalytics(${'$'}id:ID=\"${id}\") {profile(id:${'$'}id) {bestRecords(limit: 30) {score,details{perfect,great,good,bad,miss,maxCombo},mods,chart{name,difficulty,notesCount,level {title}},accuracy,rating}}}\"}"
        val data = """{"operationName":null,
            |"variables":{},
            |"query":"{profile(uid:\"$playerName\"){bestRecords(limit:$count){score,mods,accuracy,rating,details{perfect,great,good,bad,miss,maxCombo},chart{type,difficulty,notesCount,level{title,bundle{backgroundImage{original,thumbnail}}}}}}}"}""".trimMargin()
        val body = data.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://services.cytoid.io/graphql")
            .post(body)
            .build()
        val result = withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            when (response.code) {
                200 -> response.body?.string()
                404 -> throw Exception("未找到玩家")
                else -> throw Exception("Unknown Exception:HTTP response code ${response.code}")
            }
        }
        if (result == null) {
            throw Exception("Request failed")
        } else {
            return json.decodeFromString(result)
        }
    }
}