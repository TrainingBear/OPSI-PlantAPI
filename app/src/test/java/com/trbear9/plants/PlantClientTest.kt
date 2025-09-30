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
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO

class PlantClientTest {
    val log = LoggerFactory.getLogger(PlantClientTest::class.java)
    val template = RestTemplate()
    val objectMapper = ObjectMapper()
    val client = PlantClient("TrainingBear/84d0e105aaabce26c8dfbaff74b2280e")

    @Test
    fun sendPacket() {
        client.addProvider("TrainingBear/84d0e105aaabce26c8dfbaff74b2280e")
        val file = File("fast_api/uploaded_images/humus-110.jpg")
        val bos = ByteArrayOutputStream();
        val img = ImageIO.read(file)
        ImageIO.write(img, "jpg", bos);

        val data = UserVariable()

        val soil = SoilParameters.ANDOSOL
        soil!!.depth = E.DEPTH.medium
        data.image = bos.toByteArray()
        data.filename = file.name
        data.soil = soil
        try {
            runBlocking{
                val response = client.sendPacket(data)
//                log.info(objectMapper.readTree(
//                    objectMapper.writeValueAsString(response)
//                ).toPrettyString())
                log.info("Request done!")
                log.info("{}", response!!.tanaman.size)
                for (plants in response!!.tanaman.values) {
                    for (plant in plants) {
                        val img = plant.fullsize
                        if(img==null) continue
//                        client.loadImage(img) { inputStream ->
//                            val out = File("test/out")
//                            out.mkdirs()
//                            val file = File(out, img)
//                            inputStream.use {
//                                Files.copy(it, file.toPath())
//                            }
//                        }
                        break
                    }
                    break
                }
            }
        } catch (e: Exception) {
            log.info("The server is offline")
            log.error(e.message)
            e.printStackTrace()
        }
    }

    @Test
    fun sendAllSoil(){

    }

    @Test
    fun getImage() {
        val img = "Albizia falcataria fullsize.jpg"
        runBlocking {
            client.loadImage(img, {input ->
                val out = File("test/out")
                out.mkdirs()
                val file = File(out, img)
                input.use { input ->
                    file.outputStream().use {
                        input.copyTo(file.outputStream())
                    }
                }
            })
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