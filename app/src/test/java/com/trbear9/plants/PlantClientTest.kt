package com.trbear9.plants

import com.trbear9.plants.PlantClient.Companion.client
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PlantClientTest {
    @Test
    fun getUrl() {
        val url = PlantClient.url
        println(url)
    }

}