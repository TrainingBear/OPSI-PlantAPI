package com.trbear9.plants

import com.trbear9.plants.PlantClient.Companion.client
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class PlantClientTest {
    val log = LoggerFactory.getLogger(PlantClientTest::class.java)
    @Test
    fun getUrl() {
        val url = PlantClient.getUrl()
        log.info(url)
    }

}