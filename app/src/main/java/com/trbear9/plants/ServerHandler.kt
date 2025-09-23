package com.trbear9.plants

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.core.StreamWriteConstraints
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.alexdlaird.ngrok.NgrokClient
import com.github.alexdlaird.ngrok.protocol.CreateTunnel
import com.github.alexdlaird.ngrok.protocol.Proto
import com.github.alexdlaird.ngrok.protocol.Tunnel
import com.openmeteo.api.Forecast
import com.openmeteo.api.OpenMeteo
import com.openmeteo.api.common.time.Date
import com.openmeteo.api.common.units.TemperatureUnit
import com.trbear9.plants.E.*
import com.trbear9.plants.api.GeoParameters
import com.trbear9.plants.api.Plant
import com.trbear9.plants.api.Response
import com.trbear9.plants.api.SoilParameters
import com.trbear9.plants.api.UserVariable
import lombok.Getter
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.net.URI
import java.time.Duration
import javax.imageio.ImageIO
import kotlin.collections.HashMap
import kotlin.collections.MutableMap

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@Slf4j
@RestController
@Getter
class ServerHandler {
    @RequestMapping( "/", method = [RequestMethod.HEAD])
    fun head() : ResponseEntity<Void> {
        return ResponseEntity.ok().build()
    }

    @GetMapping("/")
    fun who(): String {
        return """
                            
                                        TIM OPSI SMANEGA 2025
                                        oleh Kukuh & Refan.
                RestAPI has been built by kujatic (trbear) -> https://github.com/TrainingBear (opensource)
                BIG SHOUTOUT TO JASPER                vvvvvvvvvvvvvvvvvvvvvvvvv
                Join komunitas discord kami (Jasper): https://discord.gg/fbAZSd3Hf2
                
                """.trimIndent()
    }

    val rag = mutableMapOf<String, JsonNode>()
    init {
        val dir = File("cache/responses")
        if (dir.mkdirs()) log.info("Directory created: {}", dir.absolutePath)
        for(file in dir.listFiles()){
            if(file.name.endsWith(".json"))
            rag[file.name.replace(".json", "")] = objectMapper.readTree(file)
        }
    }
    private fun rag(input: String?, namaIlmiah: String): JsonNode {
        if(rag.containsKey(namaIlmiah)) return rag[namaIlmiah]!!;
        val dir = File("cache/responses")
        if (dir.mkdirs()) log.info("Directory created: {}", dir.absolutePath)
        val file = File(dir, "$namaIlmiah.json")

        val header = HttpHeaders()
        header.contentType = MediaType.APPLICATION_JSON
        header.setBearerAuth(open_ai_key)

        val body: MutableMap<String?, Any?> = HashMap()
        body.put("model", "o3")
        body.put("input", input)

        val request = HttpEntity(body, header)

        val respon = template.postForEntity(
            "https://api.openai.com/v1/responses",
            request,
            String::class.java
        ).body
        Thread(){
            objectMapper.writeValue(file, respon)
        }.start()
        rag[namaIlmiah] = objectMapper.readTree(respon)
        return rag[namaIlmiah]!!
    }

    @PostMapping("/process")
    @Throws(IOException::class)
    fun process(@RequestBody data: UserVariable): String? {
        val hashCode = data.hash
        File("cache").mkdirs()
        val hash = File("cache/$hashCode.json")
        if(hash.exists()){
            return objectMapper.readTree(hash).toPrettyString()
        }

        var totalTime = 0.0
        log.info("POST /predict")
        val image = data.image
        var start = System.currentTimeMillis()
        val prediction = FastApiService.predict(image)
        var took = (System.currentTimeMillis() - start).toDouble() / 1000000.0
        totalTime += took
        val max = FastApiService.argmax(prediction)
        val soil = FastApiService.soil[max]
        val soilName = FastApiService.label[max]
        log.info("Soil: {}", soilName)

        //fetching
        data.fetch {
            if(it is GeoParameters) meteo(it)
            if(it is SoilParameters) {
                it.texture = soil.texture;
                it.fertility = soil.fertility;
                it.drainage = soil.drainage;
                it.pH = soil.pH;
            }
        }

        val processedData = DataHandler.process(data)
        val response = Response()
        response.soilPrediction = prediction
        response.predict_time = took
        response.soilName = soilName
        start = System.currentTimeMillis()

        var total = 0
        for (i in processedData.keys) {
            for (ecorecord in processedData[i]!!) {
                total++
                val plant = Plant()
                val namaIlmiah = ecorecord.get(Science_name)
                plant.nama_ilmiah = namaIlmiah
                plant.min_panen = ecorecord.get(MIN_crop_cycle).toInt()
                plant.max_panen = ecorecord.get(MAX_crop_cycle).toInt()
                plant.family = ecorecord.get(Family)
                plant.kategori = ecorecord.get(Category)

                val node: JsonNode = rag[namaIlmiah]?: run {
                    val query = StringBuilder(
                       "$namaIlmiah, generate this plants description, Crop production system(categorized by their scale and objectives)," +
                       " guide & care (include watering, pruning, fertilization, sunlight, pest & disease" +
                       " management), common name(in indonesia), and level of difficulty of care(MEDIUM, EASY, HARD).\n"
                    )
                    query.append(
                        "generate them in JSON, with format of: {" +
                        "plant_care: {\"watering:output, pruning:output, ..:output\"}," +
                        " difficulty:output," +
                        " description:output," +
                        " product_system:{" +
                                "rumah_tangga:output," +
                                "komersial:output," +
                                "industri:output}," +
                        " common_name:output," +
                        " prune_guide: \"a youtube(or blog as alternative) link that refer prune method for this plant or either just say this plant cant be pruned\"" +
                        "}"
                    ).append('\n')
                    query.append("generate the output in indonesian language")
                    rag(query.toString(), namaIlmiah)
                }
                write(plant, node)
                response.put(i, plant)
            }
        }
        took = (System.currentTimeMillis() - start).toDouble() / 1000000.0
        response.process_time = took
        response.took = took + totalTime
        response.total = total
        response.hashCode = hashCode

        val value: String? = objectMapper.writeValueAsString(response)
        synchronized(hash.absolutePath.intern()){
            objectMapper.writeValue(hash, value!!)
        }
        return value
    }

    /**
     *
     * @param plant objek yang akan di tulis
     * @param tree rag response
     */
    private fun write(plant: Plant, tree: JsonNode) {
        val node = objectMapper.readTree(
            tree["output"][1]["content"][0]["text"].asText()
                .removePrefix("```json\n")
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .removeSuffix("\n```")
                .trim()
        );
        plant.prune_url = node["prune_guide"].asText()
        plant.difficulty = node["difficulty"].asText()
        plant.nama_umum = node["common_name"].asText()
        plant.description = node["description"].asText()
//        if(debug) log.info(node.toPrettyString())

        //typo: product_sytem -> product_system
        val sytem = node["product_sytem"]?:node["product_system"]
        plant.kultur.put("rumah_tangga", sytem["rumah_tangga"].asText())
        plant.kultur.put("komersial", sytem["komersial"].asText())
        plant.kultur.put("industri", sytem["industri"].asText())
        node["plant_care"].fieldNames().forEach {
            plant.perawatan.put(it, node["plant_care"][it].asText())
        }
        plant.genus = plant.nama_ilmiah.split(" ")[0]


        val kew = getKew(plant.nama_ilmiah)
        if(kew == null) return
        plant.taxon = "https://powo.science.kew.org/" + kew["url"]?.asText()
        try {
            plant.fullsize = getImage(plant, kew = kew)
            plant.thumbnail = getImage(plant, kew = kew, size = "thumbnail")
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        plant.kingdom = kew["kingdom"].asText()
        plant.family = kew["family"].asText()
    }


    val kewCache = HashMap<String, JsonNode>()
    init {
        val dir = File("cache/kew_caches"); dir.mkdirs();
        for (it in dir.listFiles()) {
            var node = objectMapper.readTree(it)
            node = objectMapper.readTree(node.asText())

            if(node["totalResults"].asInt() <= 0) {
                continue
            }
            for (result in node["results"]) {
                if(result["accepted"].asBoolean()){
                    kewCache[it.name.replace(".json", "")] = result
                    log.info("Found accepted resource, with author: {}", result["author"]?.asText())
                    continue
                }
            }
            log.info("No accepted resource found, returning first result. author: {}",
                node["results"][0]["author"].asText())
        }
    }
    fun getKew(q: String): JsonNode? {
        val dir = File("cache/kew_caches"); dir.mkdirs();
        val file = File(dir, "$q.json")
        if(kewCache.containsKey(q) && kewCache[q] != null) {
            log.info("Cache hit: {}", q)
            return kewCache[q]!!
        }

        val root : JsonNode = if(!file.exists()) {
            val url = "https://powo.science.kew.org/api/1/search?q=$q"
            val response = template.getForEntity(url, String::class.java)
            Thread(){objectMapper.writeValue(file, response.body)}.start()
            objectMapper.readTree(response.body)
        } else {
            throw NullPointerException("Cant find result")
        }

        if(root["totalResults"].asInt() <= 0) {
            return null;
        }
        for (result in root["results"]) {
            if(result["accepted"].asBoolean()){
                kewCache[q] = result
                log.info("Found accepted resource, with author: {}", result["author"]?.asText())
                return result
            }
        }

        log.info("No accepted resource found, returning first result. author: {}",
            root["results"][0]["author"].asText())
        return root["results"][0];
    }

    @JvmOverloads
    @kotlin.jvm.Throws(NullPointerException::class)
    fun getImage(plant: Plant? = null, kew: JsonNode? = null, size: String? = "fullsize") : ByteArray? {
        val q : String? = plant?.nama_ilmiah

        val dir = File("cache/images")
        dir.mkdirs()
        val file = File(dir, "$q $size.jpg")
        if(file.exists()){
            return file.readBytes()
        }
        val url = "http:" +
            if (kew != null) {
                val nodes = kew["images"]
                nodes ?: run {
                    if (debug) log.info("kew root: {}", kew.toPrettyString())
                    throw NullPointerException("No images found")
                }
                nodes[0][size].asText()
            } else {
                val kew1 = getKew(q!!)
                kew1?: return null
                val nodes = kew1["images"]
                nodes?: run {
                    if(debug) log.info("kew root: {}", kew1.toPrettyString())
                    throw NullPointerException("No images found")
                }
                nodes[0][size].asText()
            }
        val byte = URI.create(url).toURL().readBytes()

        if(!file.exists()) {
            synchronized(file.absolutePath.intern()){
                val bufferedImage = ImageIO.read(ByteArrayInputStream(byte));
                ImageIO.write(bufferedImage, "jpg", file)
            }
        }
        return byte
    }

    @OptIn(com.openmeteo.api.common.Response.ExperimentalGluedUnitTimeStepValues::class)
    fun meteo(geo: GeoParameters) {
        var max = 0.0
        var min = 0.0
        var elevation = 0f
        val meteo = OpenMeteo(geo.latitude.toFloat(), geo.longtitude.toFloat())
        val temperatur = meteo.forecast(){
            latitude = geo.latitude.toFloat()
            longitude = geo.longtitude.toFloat()
            temperatureUnit = TemperatureUnit.Celsius
            elevation
            startDate = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 60)
            endDate = Date(System.currentTimeMillis())
            daily = Forecast.Daily{
                listOf(temperature2mMin, temperature2mMax)
            }
        }.getOrThrow()
        Forecast.Daily.run {
            temperatur.daily.getValue(temperature2mMax).run {
                for (m in values.values)
                    max+= m?:28.0
                max/= values.size
            }
            temperatur.daily.getValue(temperature2mMin).run {
                for (m in values.values)
                    min+= m?:20.0
                min/= values.size
            }
        }

        val MPDL = meteo.elevation {
            latitude = (-7.257281798437764).toString()
            longitude = 110.4031409940034.toString()
        }.getOrThrow()
        for (f in MPDL.elevation) {
            elevation = f
        }
        elevation/=MPDL.elevation.size
        geo.elevation = elevation.toDouble()
        geo.min =  min
        geo.max = max
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(ServerHandler::class.java)!!
        private val factory: SimpleClientHttpRequestFactory = SimpleClientHttpRequestFactory()
        private val template: RestTemplate
        private val objectMapper = ObjectMapper()

        init {
            factory.setConnectTimeout(Duration.ofMinutes(5))
            factory.setReadTimeout(Duration.ofMinutes(5))
            template = RestTemplate(factory)
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

        val open_ai_key : String = System.getProperty("OPEN_AI_KEY")
        private val gistId = System.getProperty("GIST_ID")
        private val git_token: String = System.getProperty("GITHUB_TOKEN")
        var ngrokClient: NgrokClient? = null
        var tunnel: Tunnel? = null
        private var startTime: Long = -1
        private var debug: Boolean = true
//            System.getProperty("DEBUG", "false").toBoolean();

        @JvmStatic
        @get:Throws(JsonProcessingException::class)
        val url: String?
            get() {
                if (tunnel != null) return tunnel!!.publicUrl

                val gist =
                    "https://gist.githubusercontent.com/TrainingBear/84d0e105aaabce26c8dfbaff74b2280e/raw/url.json"
                val response =
                    template.getForEntity(gist, String::class.java)
                val json = objectMapper.readTree(response.getBody())
                return json.get("content").asText()
            }

        @JvmStatic
        fun start() {
            if (ngrokClient == null) {
                ngrokClient = NgrokClient.Builder().build()
                val address = CreateTunnel.Builder()
                    .withAddr(8080)
                    .withProto(Proto.HTTP)
                    .build()
                tunnel = ngrokClient!!.connect(address)
                val publicUrl: String? = tunnel!!.publicUrl
                startTime = System.currentTimeMillis()
                log.info("ngrok tunnel \"{}\" -> \"{}\"", tunnel!!.name, publicUrl)

                val content: MutableMap<String?, String?> = HashMap()
                content.put(
                    "content",
                    "{" +
                         "\"content\": \"" + publicUrl + "\"," +
                         "\"started\": " + startTime+
                    "}"
                )
                val json: MutableMap<String?, Any?> = HashMap()
                json.put("url.json", content)

                val file: MutableMap<String?, Any?> = HashMap()
                file.put("files", json)

                val headers = HttpHeaders()
                headers.contentType = MediaType.APPLICATION_JSON
                headers.setBearerAuth(git_token)
                val request = HttpEntity(file, headers)

                val response: ResponseEntity<String?> =
                    template.postForEntity(
                        "https://api.github.com/gists/$gistId",
                        request,
                        String::class.java
                    )
                if (response.statusCode.is2xxSuccessful) {
                    log.info("Gist updated: {}", response.body!!.substring(0, 100))
                } else {
                    log.error("Gist update failed: {}", response.getBody())
                }
            }
        }

        @JvmStatic
        fun stop() {
            if (tunnel != null && ngrokClient != null) {
                log.info("Stopping ngrok tunnel \"{}\"", tunnel!!.name)
                val content: MutableMap<String?, String?> = HashMap()
                content.put(
                    "content",
                    "{" +
                            "\"content\": \"http://localhost:8080\"," +
                            "\"started\": " + startTime + "," +
                            "\"stopped\": " + System.currentTimeMillis()+
                    "}"
                )
                val json: MutableMap<String?, Any?> = HashMap()
                json.put("url.json", content)
                val file: MutableMap<String?, Any?> = HashMap()
                file.put("files", json)

                val headers = HttpHeaders()
                headers.contentType = MediaType.APPLICATION_JSON
                headers.setBearerAuth(git_token)
                val request = HttpEntity(file, headers)

                val response: ResponseEntity<String?> =
                    template.postForEntity(
                        "https://api.github.com/gists/$gistId",
                        request,
                        String::class.java
                    )
                if (response.statusCode.is2xxSuccessful) {
                    log.info("Gist updated: {}", response.getBody())
                } else {
                    log.error("Gist update failed: {}", response.getBody())
                }
                tunnel = null
                ngrokClient!!.kill()
                ngrokClient = null
            }
        }
    }
}

class GeoLocation {
    var elevation: Float? = null
    var minTemp: Double? = null
    var maxTemp: Double? = null
    constructor(elevation: Float?, minTemp: Double, maxTemp: Double){
        this.elevation = elevation
        this.minTemp = minTemp
        this.maxTemp = maxTemp
    }
}
