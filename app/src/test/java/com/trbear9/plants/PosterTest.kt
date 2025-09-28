package com.trbear9.plants

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.trbear9.plants.E.CLIMATE
import com.trbear9.plants.E.DEPTH
import com.trbear9.plants.ServerHandler.Companion.url
import com.trbear9.plants.api.GeoParameters
import com.trbear9.plants.api.blob.Plant
import com.trbear9.plants.api.SoilParameters
import com.trbear9.plants.api.UserVariable
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.time.Duration
import javax.imageio.ImageIO

@SpringBootTest(classes = [ServerHandler::class])
class PosterTest {
    private val objectMapper = ObjectMapper()

    @Test
    @Throws(JsonProcessingException::class)
    fun postVar() {
        val file = File("fast_api/uploaded_images/aluvial-001.jpg")
        val bos = ByteArrayOutputStream()
        try {
            val img = ImageIO.read(file)
            ImageIO.write(img, "jpg", bos)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val geoParameters: GeoParameters = GeoParameters().apply{
            iklim = (CLIMATE.temperate_with_humid_winters)}
        val soilParameters = SoilParameters().apply {
            depth = (DEPTH.deep)}
        val userVariable = UserVariable()
        userVariable.geo = geoParameters
        userVariable.soil = soilParameters
        userVariable.image = bos.toByteArray()
        userVariable.computeHash()

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = mutableListOf<MediaType?>(MediaType.APPLICATION_JSON)

        val request = HttpEntity(userVariable, headers)

        try {
            val response: ResponseEntity<String?> =
                template.postForEntity(url + PlantClient.Companion.PROCESS, request, String::class.java)
            val root = objectMapper.readTree(response.getBody())
            log.info(root.toPrettyString())
        } catch (e: Exception) {
            log.error("Cant procces your request")
            log.error("Error: {}", e.message)
            e.printStackTrace()
        }
    }

    @Test
    @Throws(IOException::class)
    fun predict() {
//        File file = new File("fast_api/uploaded_images/aluvial-001.jpg");
//        float[] predict = FAService.predict(file);
//        log.info("predicts: {}", predict);
    }

    @Test
    fun makeDir() {
        val dir = File("cache/responses")
        val file = File(dir, System.nanoTime().toString() + ".json")
        if (dir.mkdirs()) {
            log.info("Directory created: {}", dir.absolutePath)
        } else {
            log.info("Directory already exists: {}", dir.absolutePath)
        }
        val json = "{\"state\": \"\test\"}"
        //        if(!file.exists()) try {
//            objectMapper.writeValue(file, json);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    @Test
    fun pykew() {
//        String urticaUrens = template.getForEntity(FAService.url+"/", String.class).getBody();
        try {
            val urticaUrens: String? =
                template.getForEntity(FastApiService.url + "/plants/" + "Urtica", String::class.java).getBody()
            log.info("{}", urticaUrens)
        } catch (e: Exception) {
            log.error("Error: {}", e.message)
        }
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun getKewImage() {
        val serverHandler = ServerHandler()
        val url = "https://powo.science.kew.org/api/1/search?q=Urtica"
        try {
            val response: ResponseEntity<String?> = template.getForEntity(url, String::class.java)
            val body = response.getBody()
            val urtica = checkNotNull(serverHandler.getKew("Urtica"))
            val plant = Plant()
            plant.nama_ilmiah = "Urtica"
            val urticas = serverHandler.getImagePath(plant)
        } catch (e: Exception) {
            log.error("Error: {}", e.message)
        }
    }

    @Test
    fun env() {
        log.info("{}", System.getenv("GIST_ID"))
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(PosterTest::class.java)
        private val requestFactory: SimpleClientHttpRequestFactory = SimpleClientHttpRequestFactory()
        private val template: RestTemplate

        init {
            requestFactory.setReadTimeout(Duration.ofDays(10))
            requestFactory.setConnectTimeout(Duration.ofDays(10))
            template = RestTemplate(requestFactory)
        }
    }
}