package com.trbear9.plants

import com.fasterxml.jackson.databind.ObjectMapper
import com.trbear9.plants.api.Response
import com.trbear9.plants.api.UserVariable
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class PlantClient {
    companion object{
        val objectMapper = ObjectMapper()
        val client = OkHttpClient()
        val providers = mutableListOf("https://gist.githubusercontent.com/TrainingBear/${System.getenv("GIST_ID")}/raw/url.json")

        @JvmStatic
        fun sendPacket(data: UserVariable): Response {
            val request = Request.Builder()
                .url(getUrl(providers.last()))
                .post(objectMapper.writeValueAsString(data).toRequestBody())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build()
            val jsonResponse= client.newCall(request).execute().body.string()
            return objectMapper.readValue(jsonResponse, Response::class.java)
        }

        @JvmStatic
                /**
                 * @param provider gist provider, the name of GitHub user. eg: TrainingBear
                 * @param id gist id provider. eg: necron8971handle2834y2hy7reimburse4ano
                 */
        fun addProvider(provider: String, id: String) {
            providers+=("https://gist.githubusercontent.com/$provider/$id/raw/url.json")
        }

        @JvmStatic
        @JvmOverloads
        fun getUrl(provider: String? = providers.last()) : String {
            val request = Request.Builder()
                .url(provider!!)
                .build()
            val response = client.newCall(request).execute()
            if(!response.isSuccessful){
                if(providers.isEmpty()) throw IllegalArgumentException("No providers available")
                providers.removeLast()
                return getUrl()
            }

            val string = response.body.string()
            val url = objectMapper.readTree(string)["content"].asText()

            val head = client.newCall(Request.Builder().url(url).head().build()).execute()
            if (head.isSuccessful || head.code >= 300 && head.code < 400) {
                return url
            }
            if(providers.isEmpty()) throw IllegalArgumentException("No providers available")
            providers.removeLast()
            return getUrl()
        }
    }
}

