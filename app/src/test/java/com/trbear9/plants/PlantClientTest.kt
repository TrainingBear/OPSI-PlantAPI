package com.trbear9.plants

import com.fasterxml.jackson.databind.ObjectMapper
import com.trbear9.plants.api.CustomParameters
import com.trbear9.plants.api.GeoParameters
import com.trbear9.plants.api.SoilParameters
import com.trbear9.plants.api.UserVariable
import org.apache.commons.lang3.ObjectUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.client.getForEntity
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

class PlantClientTest {
    val log = LoggerFactory.getLogger(PlantClientTest::class.java)
    val template = RestTemplate()
    val objectMapper = ObjectMapper()

    @Test
    fun sendPacket() {
        PlantClient.Companion.addProvider("TrainingBear", "84d0e105aaabce26c8dfbaff74b2280e")
        val resource = ClassPathResource("fast_api/uploaded_images/aluvial-001.jpg")
        val file = resource.file;
        val bos = ByteArrayOutputStream();
        val img = ImageIO.read(file)
        ImageIO.write(img, "jpg", bos);

        val data = UserVariable()

        val geo = GeoParameters()
        val soil = SoilParameters()
        val custom = CustomParameters()
        geo.iklim = E.CLIMATE.tropical_wet_and_dry
        soil.depth = E.DEPTH.medium
        data.image = bos.toByteArray()
        data.add(geo, soil, custom)

        try {
//            val plantClient = PlantClient()
//            val response = plantClient.sendPacket(data, PlantClient.PROCESS)
            val response = PlantClient.sendPacket(data, PlantClient.PROCESS)
            log.info(objectMapper.readTree(
                objectMapper.writeValueAsString(response)
            ).toPrettyString())
        } catch (e: Exception) {
            log.info("The server is offline")
            log.error(e.message)
            e.printStackTrace()
        }
    }

    @Test
    fun getUrl() {
        try {
//            val plantClient = PlantClient()
//            plantClient.addProvider("TrainingBear", "84d0e105aaabce26c8dfbaff74b2280e")
            PlantClient.Companion.addProvider("TrainingBear", "84d0e105aaabce26c8dfbaff74b2280e")
            val url = PlantClient.Companion.url
            log.info(url)
            log.info(PlantClient.debug().toString())
        } catch (e: Exception) {
            log.info("The server is offline")
            log.error(e.message)
            e.printStackTrace()
        }
    }

    @Test
    fun head(){
        try {
//            val plantClient = PlantClient()
//            val link = plantClient.url
            val link = PlantClient.url
            val head = template.exchange<String>(
                url = link!!,
                method = HttpMethod.HEAD
            )
            log.info("status code: {}", head.statusCode.is2xxSuccessful)
            log.info(head.headers.toString())
        } catch (e: Exception) {
            log.info("The server is offline")
            log.error(e.message)
            e.printStackTrace()
        }
    }
}