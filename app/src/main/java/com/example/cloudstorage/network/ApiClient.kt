package com.example.cloudstorage.network

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ApiClient {
    // 10.0.2.2 is Android Emulator's alias to host machine localhost
    var baseUrl: String = "http://10.0.2.2:5000/api"

    suspend fun getStorageOverview(): Result<StorageOverviewResponse> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/storage/overview")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = reader.readText()
                reader.close()
                val json = JSONObject(response)
                val breakdownJson = json.getJSONObject("breakdown")
                val breakdown = StorageBreakdown(
                    images = breakdownJson.optLong("images", 0),
                    documents = breakdownJson.optLong("documents", 0),
                    videos = breakdownJson.optLong("videos", 0),
                    audio = breakdownJson.optLong("audio", 0),
                    others = breakdownJson.optLong("others", 0)
                )
                Result.success(
                    StorageOverviewResponse(
                        totalLimit = json.getLong("totalLimit"),
                        totalUsed = json.getLong("totalUsed"),
                        percentage = json.getInt("percentage"),
                        breakdown = breakdown,
                        fileCount = json.getInt("fileCount"),
                        trashCount = json.getInt("trashCount")
                    )
                )
            } else {
                Result.failure(Exception("HTTP Error: ${conn.responseCode}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
