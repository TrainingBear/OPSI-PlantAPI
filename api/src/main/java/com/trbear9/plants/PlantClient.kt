package com.trbear9.plants

import com.fasterxml.jackson.databind.ObjectMapper
import com.trbear9.plants.api.Response
import com.trbear9.plants.api.UserVariable
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.Duration

class PlantClient {
    companion object{
        const val PROCESS = "/process"
        val objectMapper = ObjectMapper()
        val client = OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofMinutes(3))
            .writeTimeout(Duration.ofMinutes(3))
            .callTimeout(Duration.ofMinutes(5))
            .build()
        val providers = mutableListOf("https://gist.githubusercontent.com/TrainingBear/${System.getenv("GIST_ID")}/raw/url.json")
        @JvmField
        var url: String? = getUrl()

        @JvmStatic
        fun sendPacket(data: UserVariable, type: String? = PROCESS): Response {
            val request = Request.Builder()
                .url((url ?: getUrl(providers.last())) + type)
                .post(objectMapper.writeValueAsString(data).toRequestBody())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build()
            println("POSTING ${request.url}")
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
        private fun getUrl(provider: String? = providers.last(), tries: Set<String>? = mutableSetOf()) : String {
            return url?: run {
                val request = Request.Builder()
                    .url(provider!!)
                    .build()
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    if (providers.isEmpty()) throw ProviderException("No providers available", tries!!)
                    providers.removeLast()
                    return getUrl(tries = tries)
                }

                val string = response.body.string()
                val url = objectMapper.readTree(string)["content"].asText()

                val head = client.newCall(Request.Builder().url(url).head().build()).execute()
                if (head.code == 200) {
                    return url
                }
                if (providers.isEmpty()) throw ProviderException("No providers available", tries!!)
                providers.removeLast()
                return getUrl(tries = tries)
            }
        }
        fun debug(provider: String? = providers.last()) : Int {
             val request = Request.Builder()
                .url(provider!!)
                .build()
            val response = client.newCall(request).execute()

            val string = response.body.string()
            val url = objectMapper.readTree(string)["content"].asText()

            val head = client.newCall(Request.Builder().url(url).head().build()).execute()
            return head.code
        }
    }
}

