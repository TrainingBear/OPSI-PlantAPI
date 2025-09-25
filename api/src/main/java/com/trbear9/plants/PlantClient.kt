package com.trbear9.plants

import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.core.StreamWriteConstraints
import com.fasterxml.jackson.databind.ObjectMapper
import com.trbear9.plants.api.Response
import com.trbear9.plants.api.UserVariable
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.Duration
import java.util.Stack

class PlantClient {
    companion object {
        @JvmField
        val PROCESS = "/process"
        val objectMapper = ObjectMapper()
        init {
            objectMapper.factory.setStreamReadConstraints(
                StreamReadConstraints.builder()
                    .maxStringLength(1_000_000)
                    .build()
            ).setStreamWriteConstraints(
                StreamWriteConstraints.builder()
                    .maxNestingDepth(1_000_000)
                    .build()
            )
        }
        val client = OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofMinutes(3))
                .writeTimeout(Duration.ofMinutes(3))
                .callTimeout(Duration.ofMinutes(5))
                .build()
        @JvmStatic
        fun debug(plantClient: PlantClient, provider: String? = plantClient.providers.last()): Int {
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
    val objectMapper = ObjectMapper()
    var providers = mutableListOf("https://gist.githubusercontent.com/null/null/raw/url.json")
    val url: String get() = getUrll()
    constructor(vararg provider: String, size : Int = 1_000_000){
        provider.forEach { addProvider(it) }
        objectMapper.factory.setStreamReadConstraints(
            StreamReadConstraints.builder()
                .maxStringLength(size)
                .build()
        ).setStreamWriteConstraints(
            StreamWriteConstraints.builder()
                .maxNestingDepth(size)
                .build()
        )
    }

    /**
     * Ukuran maksimal file respon. 1000 = 1mb
     * @param int 1 = 1kb
     */
    fun maxSize(int: Int){
        objectMapper.factory.setStreamReadConstraints(
            StreamReadConstraints.builder()
                .maxStringLength(int)
                .build()
        ).setStreamWriteConstraints(
            StreamWriteConstraints.builder()
                .maxNestingDepth(int)
                .build()
        )
    }

    fun sendPacket(data: UserVariable, type: String? = PROCESS,
                   onResponse: (Response) -> Unit? = {}, callBack: (okhttp3.Callback)? = null
                   ) {
        data.computeHash()
        val request = Request.Builder()
            .url(url + type)
            .post(objectMapper.writeValueAsString(data).toRequestBody())
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build()
        println("POSTING ${request.url}")
        try {
            client.newCall(request).enqueue(callBack ?:
                object : okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                        e.printStackTrace()
                    }
                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        onResponse(
                            objectMapper.readValue(response.body.string(), Response::class.java)
                        )
                    }
                }
            )
        } catch (e: Exception) {
            throw e
        }
    }

    fun GET(map: String = "/") : String?{
        val request = Request.Builder()
            .url(url + map)
            .get()
            .build()
        try {
            client.newCall(request).execute().use {
                return it.body.string()
            }
        } catch (e: Exception) {
            throw e
        }
    }
                /**
                 * @param providerId gist provider, the name of GitHub user.
                 * eg: TrainingBear/necron8971handle2834y2hy7reimburse4ano
                 */
        fun addProvider(providerId: String) {
            val s = providerId.split('/')
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
            while (!prov.isEmpty()){
                val provider = prov.pop()
                val request = Request.Builder()
                    .url(provider)
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
                        println("RECEIVED $url")
                        return url
                    }
                    tries += url
                }
            }
            throw ProviderException("No providers available", tries)
        }
}


