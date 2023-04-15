package com.lyneon.cytoidinfo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private val json = Json { ignoreUnknownKeys = true }

object NetRequest {
    suspend fun getPlayerProfile(playerName:String): PlayerProfile {
        val site = URL("https://services.cytoid.io/profile/$playerName/details")
        val huc = withContext(Dispatchers.IO) {
            site.openConnection()
        } as HttpURLConnection
        huc.requestMethod = "GET"
        huc.connectTimeout =8000
        huc.readTimeout=8000
        val result = StringBuilder()
        return if (huc.responseCode==200){
            val br = BufferedReader(InputStreamReader(huc.inputStream))
            br.use {
                br.forEachLine {
                    result.append(it)
                }
            }
            huc.disconnect()
            json.decodeFromString(result.toString())
        }else {
            huc.disconnect()
            throw Exception("Request failed:${huc.responseCode}")
        }
    }
}