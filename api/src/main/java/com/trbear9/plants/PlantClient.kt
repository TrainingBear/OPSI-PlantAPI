package com.trbear9.plants

import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.core.StreamWriteConstraints
import com.fasterxml.jackson.databind.ObjectMapper
import com.trbear9.plants.api.Response
import com.trbear9.plants.api.UserVariable
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.awt.List
import java.time.Duration
import java.util.Stack

class PlantClient {
    companion object {
        const val PROCESS = "/process"
        val objectMapper = ObjectMapper()
                val client = OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofMinutes(3))
            .writeTimeout(Duration.ofMinutes(3))
            .callTimeout(Duration.ofMinutes(5))
            .build()
        init {
            objectMapper.factory.setStreamReadConstraints(
                StreamReadConstraints.builder()
                    .maxStringLength(1_000_000_000)
                    .build()
            ).setStreamWriteConstraints(
                StreamWriteConstraints.builder()
                    .maxNestingDepth(1_000_000_000)
                    .build()
            )
        }
        var providers = mutableListOf("https://gist.githubusercontent.com/null/null/raw/url.json")
        val url: String get() = getUrll()

        fun sendPacket(data: UserVariable, type: String? = PROCESS): Response? {
            data.computeHash()
            val request = Request.Builder()
                .url(url + type)
                .post(objectMapper.writeValueAsString(data).toRequestBody())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build()
            println("POSTING ${request.url}")
            try {
                client.newCall(request).execute().use {
                    return objectMapper.readValue(it.body.string(), Response::class.java)
                }
            } catch (e: Exception) {
                throw e
            }
        }

                /**
                 * @param provider_id gist provider, the name of GitHub user.
                 * eg: TrainingBear/necron8971handle2834y2hy7reimburse4ano
                 */
        fun addProvider(provider_id: String) {
            val s = provider_id.split('/')
            addProvider(s[0], s[1])
        }
                /**
                 * @param provider gist provider, the name of GitHub user. eg: TrainingBear
                 * @param id gist id provider. eg: necron8971handle2834y2hy7reimburse4ano
                 */
        fun addProvider(provider: String, id: String) {
            providers+=("https://gist.githubusercontent.com/$provider/$id/raw/url.json")
            println("Provider has been added, size: ${providers.size}")
        }

        private fun getUrll() : String {
            val prov: Stack<String> = Stack<String>().apply{ addAll(providers)}
            val tries: MutableSet<String> = mutableSetOf()
            println("Getting url with providers size: ${prov.size}")
            while (!prov!!.isEmpty()){
                val provider = prov.pop()
                val request = Request.Builder()
                    .url(provider!!)
                    .build()
                client.newCall(request).execute().use { response ->
                    println("GETTING $provider")
                    if (!response.isSuccessful) {
                        tries += provider
                        continue
                    }

                    val string = response.body.string()
                    val url = objectMapper.readTree(string)["content"].asText()

                    val head = client.newCall(Request.Builder().url(url).head().build()).execute()
                    if (head.code == 200) {
                        return url
                    }
                    tries += url
                }
            }
            throw ProviderException("No providers available", tries)
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


