package com.trbear9.plants

import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.core.StreamWriteConstraints
import com.fasterxml.jackson.databind.ObjectMapper
import com.trbear9.plants.api.Response
import com.trbear9.plants.api.UserVariable
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.source
import java.io.InputStream
import java.time.Duration
import java.util.Stack
import kotlin.coroutines.resume

class PlantClient {
    companion object {
        @JvmField
        val PROCESS = "/process"
        val IMAGE = "/images"
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
            .readTimeout(Duration.ofMinutes(30))
            .writeTimeout(Duration.ofMinutes(30))
            .callTimeout(Duration.ofMinutes(50))
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

    constructor(vararg provider: String, size: Int = 1_000_000) {
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
    fun setMaxSize(int: Int) {
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

    suspend fun check(url: String): String? = suspendCancellableCoroutine {
        sus ->
        val request = Request.Builder()
            .url(url)
            .build()
        val call = client.newCall(request)
        call.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                e.printStackTrace()
                sus.resume(null)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                println("${response.code} ${response.message}");
                sus.resume(response.body.string())
            }
        })
        sus.invokeOnCancellation { call.cancel() }
    }

    suspend fun loadImage(name: String, result: (InputStream) -> Unit) {
        val url = getUrl()
        return suspendCancellableCoroutine { sus ->
            val request = Request.Builder()
                .url("$url$IMAGE/$name")
                .build()
            val call = client.newCall(request)
            call.enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {
                    response.use {
                        it.body.byteStream().use { input ->
                            result(input)
                            sus.resume(Unit)
                        }
                    }
                }
            })
        }
    }

    suspend fun sendPacket(data: UserVariable, url: String? = null
    ): Response? {
        val url = url?:getUrl();
        return suspendCancellableCoroutine { sus ->
            data.computeHash()
            val request = Request.Builder()
                .url(url + PROCESS)
                .post(objectMapper.writeValueAsString(data).toRequestBody())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build()
            println("POSTING ${request.url}")
            try {
                val call = client.newCall(request)
                sus.invokeOnCancellation { call.cancel() }
                call.enqueue(object : Callback {
                    override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                        e.printStackTrace()
                        sus.resume(null)
                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        sus.resume(
                            objectMapper.readValue(response.body.string(), Response::class.java)
                        )
                    }
                })
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun GET(map: String = "/", url: String? = null): okhttp3.Response {
        val url = url?:getUrl()
        return suspendCancellableCoroutine { sus ->
            val request = Request.Builder()
                .url(url + map)
                .get()
                .build()
            try {
                val call = client.newCall(request)
                sus.invokeOnCancellation { call.cancel() }
                call.enqueue(object : okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                        e.printStackTrace()
                    }
                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        sus.resume(response)
                    }
                })
            } catch (e: Exception) {
                throw e
            }
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
        providers += ("https://gist.githubusercontent.com/$provider/$id/raw/url.json")
        println("Provider has been added, size: ${providers.size}")
    }

    suspend fun getUrl(): String? = suspendCancellableCoroutine {
        sus ->
        val prov: Stack<String> = Stack<String>().apply { addAll(providers) }
        val tries: MutableSet<String> = mutableSetOf()
        fun find() {
            if (prov.isEmpty()) {
                sus.resume(null)
                println("Cant find any active provider")
                throw ProviderException("No provider available", tries)
            }

            val provider = prov.pop()
            try{
                provider.toHttpUrl()
            } catch (e: Exception){
                tries += provider
                find()
                e.printStackTrace()
                return
            }
            val request = Request.Builder()
                .url(provider)
                .build()
            println("GETTING $provider")
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    e.printStackTrace()
                    tries += provider
                    find()
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    if (!response.isSuccessful) {
                        tries += provider
                        find()
                        return
                    }
                    val string = response.body.string()
                    val url = objectMapper.readTree(string)["content"].asText()

                    client.newCall(
                        Request.Builder()
                            .url(url)
                            .head()
                            .build()
                    ).enqueue(object : Callback {
                        override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                            e.printStackTrace()
                            tries += url
                            find()
                        }

                        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                            if (response.code == 200) {
                                println("RECEIVED $url")
                                sus.resume(url)
                            } else {
                                tries += url
                                find()
                            }
                        }
                    })
                }
            })
        }
        find()
    }
}


