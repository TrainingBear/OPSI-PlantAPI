package com.trbear9.plants

import com.fasterxml.jackson.databind.ObjectMapper
import com.trbear9.plants.api.CustomParameters
import com.trbear9.plants.api.GeoParameters
import com.trbear9.plants.api.SoilParameters
import com.trbear9.plants.api.UserVariable
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class PlantClientTest {
    val log = LoggerFactory.getLogger(PlantClientTest::class.java)
    val template = RestTemplate()
    val objectMapper = ObjectMapper()
    val client = PlantClient("TrainingBear/84d0e105aaabce26c8dfbaff74b2280e")

    @Test
    fun sendPacket() {
        client.addProvider("TrainingBear/84d0e105aaabce26c8dfbaff74b2280e")
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
        data.filename = resource.file.name
        data.add(geo, soil, custom)

        try {
            runBlocking{
                val response = client.sendPacket(data)
                log.info(objectMapper.readTree(
                    objectMapper.writeValueAsString(response)
                ).toPrettyString())
            }
        } catch (e: Exception) {
            log.info("The server is offline")
            log.error(e.message)
            e.printStackTrace()
        }
    }

    @Test
    fun getUrl() {
        try {
            runBlocking {
            val url = client.getUrl()
                log.info(url)
                log.info(PlantClient.debug(client).toString())
            }
        } catch (e: Exception) {
            log.info("The server is offline")
            log.error(e.message)
            e.printStackTrace()
        }
    }

    @Test
    fun head(){
        try {
            runBlocking {
            val link = client.getUrl()
                val head = template.exchange<String>(
                    url = link!!,
                    method = HttpMethod.HEAD
                )
                log.info("status code: {}", head.statusCode.is2xxSuccessful)
                log.info(head.headers.toString())
            }
        } catch (e: Exception) {
            log.info("The server is offline")
            log.error(e.message)
            e.printStackTrace()
        }
    }
}