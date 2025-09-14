package com.trbear9.plants

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request

class PlantClient {

    companion object{
        val objectMapper = ObjectMapper()
        val client = OkHttpClient()

        @JvmStatic
        val url: String?
            get() {
                val gist =
                    "https://gist.githubusercontent.com/TrainingBear/84d0e105aaabce26c8dfbaff74b2280e/raw/url.json"
                val request = Request.Builder()
                    .url(gist)
                    .build()

                val response = client.newCall(request).execute()
                val string = response.body.string()
                val url = objectMapper.readTree(string)["content"].asText()
                return url
            }
    }
}