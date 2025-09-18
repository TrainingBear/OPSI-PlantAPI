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
        val file = File("fast_api/uploaded_images/aluvial-001.jpg");
        val bos = ByteArrayOutputStream();
        val img = ImageIO.read(file)
        ImageIO.write(img, "jpg", bos);

        val data = UserVariable()

        val geo = GeoParameters()
        val soil = SoilParameters()
        val custom = CustomParameters()
        geo.iklim = E.CLIMATE.tropical_wet
        soil.depth = E.DEPTH.medium
        data.image = bos.toByteArray()
        data.add(geo, soil, custom)

        val response = PlantClient.sendPacket(data, PlantClient.PROCESS)
        log.info(objectMapper.readTree(
            objectMapper.writeValueAsString(response)
        ).toPrettyString()
        )
    }

    @Test
    fun getUrl() {
        val url = PlantClient.url
        log.info(url)
        log.info(PlantClient.debug().toString())
    }

    @Test
    fun head(){
        val link = PlantClient.url
        val head = template.exchange<String>(
            url = link!!,
            method = HttpMethod.HEAD
        )
        log.info("status code: {}", head.statusCode.is2xxSuccessful)
        log.info(head.headers.toString())
    }
}