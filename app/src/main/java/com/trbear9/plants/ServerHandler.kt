package com.trbear9.plants

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.core.StreamWriteConstraints
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
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
import com.trbear9.plants.api.blob.Plant
import com.trbear9.plants.api.Response
import com.trbear9.plants.api.UserVariable
import com.trbear9.plants.api.blob.SoilCare
import jakarta.servlet.http.HttpServletResponse
import lombok.Getter
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.io.File
import java.io.IOException
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.Duration
import kotlin.collections.HashMap
import kotlin.collections.MutableMap
import kotlin.jvm.java

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

    val plantDir = File("cache/plant")
    val soilDir = File("cache/soil")
    val plantResponse = mutableMapOf<String, Plant>()
    val soilCare = mutableMapOf<String, SoilCare>()
    init {
        var start = System.nanoTime()
        if (plantDir.mkdirs()) log.info("Directory created: {}", soilDir.absolutePath)
        for(file in plantDir.listFiles()){
            if(file.name.endsWith(".json")) {
                if(debug) log.info("Loading {}", file.name)
                try {
                    plantResponse[file.name.replace(".json", "")] = run {
                        val tree = objectMapper.readTree(file)
                        try {
                            objectMapper.readValue<Plant>(
                                sanitize(tree["output"][1]["content"][0]["text"].asText()),
                                Plant::class.java
                            )
                        } catch (e: Exception){
                            log.info(sanitize(tree["output"][1]["content"][0]["text"].asText()))
                            throw e
                        }
                    }
                } catch (e: Exception) {
                    log.error("Something went wrong: {}", e.message)
                    val trash = File(plantDir, "trash")
                    Files.move(file.toPath(), trash.toPath(), StandardCopyOption.REPLACE_EXISTING)
                    log.warn("Deleted {}", file.name)
                    continue
                }
            }
        }
        log.info("Plant care loaded in {} ms", (System.nanoTime() - start) / 1000000)

        start = System.nanoTime()
        if (soilDir.mkdirs()) log.info("Directory created: {}", soilDir.absolutePath)
        for(file in soilDir.listFiles()){
            if(file.name.endsWith(".json")) {
                soilCare[file.name.replace(".json", "")] = run {
                    val tree = objectMapper.readTree(file)
                    try {
                        objectMapper.readValue<SoilCare>(
                            sanitize(tree["output"][1]["content"][0]["text"].asText()),
                            SoilCare::class.java)
                    } catch (e: Exception) {
                        log.error("Something went wrong: {}", e.message)
                        val trash = File(soilDir, "trash")
                        Files.move(file.toPath(), trash.toPath(), StandardCopyOption.REPLACE_EXISTING)
                        log.warn("Deleted {}", file.name)
                        continue
                    }
                }
                if(debug) log.info("Loaded {}", file.name)
            }
        }
        log.info("Soil care loaded in {} ms", (System.nanoTime() - start) / 1000000)
    }
    private fun <T> rag(input: String?, name: String, clazz: Class<T>): T {
        if(clazz == Plant::class.java) {
            if (plantResponse.containsKey(name)) return plantResponse[name]!! as T;
            val file = File(plantDir, "$name.json")

            val header = HttpHeaders()
            header.contentType = MediaType.APPLICATION_JSON
            header.setBearerAuth(open_ai_key)

            val body: MutableMap<String?, Any?> = HashMap()
            body.put("model", "o4-mini")
            body.put("input", input)

            val request = HttpEntity(body, header)

            var respon: String? = null
            try {
                respon = template.postForEntity(
                    "https://api.openai.com/v1/responses",
                    request,
                    String::class.java
                ).body
            }catch (e: Exception){
                log.error("Something wrong when getting $name from OpenAI")
                throw e
            }
            var tree = objectMapper.readTree(respon)
            synchronized(file.absolutePath.intern()) {
                objectMapper.writeValue(file, tree)
            }
            tree = objectMapper.readTree(sanitize(tree["output"][1]["content"][0]["text"].asText()))
            try {
                plantResponse[name] = objectMapper.treeToValue(tree, Plant::class.java)
            }catch (e: UnrecognizedPropertyException){
                log.info(tree.toPrettyString())
                throw e
            }
            return plantResponse[name]!! as T
        } else if(clazz == SoilCare::class.java) {
            if (soilCare.containsKey(name)) return soilCare[name]!! as T;
            val file = File(soilDir, "$name.json")

            val header = HttpHeaders()
            header.contentType = MediaType.APPLICATION_JSON
            header.setBearerAuth(open_ai_key)

            val body: MutableMap<String?, Any?> = HashMap()
            body.put("model", "o3")
            body.put("input", input)

            val request = HttpEntity(body, header)
            var respon: String?
            try {
                respon = template.postForEntity(
                    "https://api.openai.com/v1/responses",
                    request,
                    String::class.java
                ).body
            }catch (e: Exception){
                log.error("Something wrong when getting $name from OpenAI")
                throw e
            }
            var tree = objectMapper.readTree(respon)
            synchronized(file.absolutePath.intern()) {
                objectMapper.writeValue(file, tree)
            }
            tree = objectMapper.readTree(sanitize(tree["output"][1]["content"][0]["text"].asText()))
            soilCare[name] = objectMapper.treeToValue(tree, SoilCare::class.java)
            return soilCare[name]!! as T
        }
        throw IllegalArgumentException("Invalid class")
    }

    fun sanitize(text: String): String {
        return text
            .replace(Regex("^```(json)?\\s*"), "")
            .replace(Regex("```$"), "")
            .replace(Regex("\",\"}"), "\"}")
            .trim()
    }


    @PostMapping("/process")
    @Throws(IOException::class)
    fun process(@RequestBody data: UserVariable, result: HttpServletResponse) {
        result.contentType = "application/json"
        val hashCode = data.hash
        File("cache").mkdirs()
        val hash = File("cache/$hashCode.json")
        if (hash.exists()) {
            hash.inputStream().use { input ->
                result.outputStream.use {
                    input.copyTo(it)
                }
            }
            return
        }

        val response = Response()
        val sumber = data.geo
        log.info("request from lon: ${sumber.longtitude} lat: ${sumber.latitude} -> POST /process ")
        var totalTime = 0.0
        val image = data.image
        var start = System.nanoTime()
        val prediction = FastApiService.predict(image, data.filename ?: run {
            log.error("File name cannot be null!")
            objectMapper.writeValue(result.outputStream, response)
            return
        })
        var took = (System.nanoTime() - start).toDouble() / 1000000.0
        totalTime += took
        val max = FastApiService.argmax(prediction)
        val resultSoil = FastApiService.soil[max]
        val soilName = FastApiService.label[max]
        log.info("Soil: {}", soilName)

        response.soilPrediction = prediction
        response.predict_time = took
        response.soilName = soilName

        var flag = true;
        for (it in prediction) {
            if (it > 0.5f) {
                flag = false
                break
            }
        }
        if (flag) {
            log.warn("The soil is not valid!")
            response.error = "Gambar tidak menunjukan adanya keberadaan tanah!"
            objectMapper.writeValue(result.outputStream, response)
            return
        }

        //fetching
        data.geo.let {
            meteo(it)
            response.geo = data.geo
        }
        data.soil.let {
            it.texture = resultSoil.texture;
            it.fertility = resultSoil.fertility;
            it.drainage = resultSoil.drainage;
            it.pH = it.pH ?: resultSoil.pH

            response.soil = it
        }

        val processedData = DataHandler.process(data)
        start = System.currentTimeMillis()

        var target = 0
        for (i in processedData.keys)
            target += processedData[i]!!.size
        var total = 0
        for (i in processedData.keys) {
            for (ecorecord in processedData[i]!!) {
                total++
                val namaIlmiah = ecorecord.get(Science_name)
                if (debug) log.debug("Processing $namaIlmiah {}/{}", total, target)
                var plant: Plant?
                try {
                     plant = plantResponse[namaIlmiah] ?: run {
                        rag(
                            """
                        You are given a plant with the scientific name: "$namaIlmiah".
                        Generate structured information in **valid JSON only** (no extra text).
                    
                        The JSON must follow this schema:
                        {
                          "plant_care": {
                            "watering": "...",
                            "pruning": "...",
                            "fertilization": "...",
                            "sunlight": "...",
                            "pest_disease_management": "..."
                          },
                          "difficulty": "EASY | MEDIUM | HARD",
                          "description": "...",
                          "product_system": {
                            "rumah_tangga": "...",
                            "komersial": "...",
                            "industri": "..."
                          },
                          "common_name": "...",
                          "prune_guide": "YouTube/Blog link OR 'Tanaman ini tidak dapat dipangkas'"
                        }
                    
                        Requirements:
                        - All values must be written in **Indonesian language**.
                        - Do not add explanations outside JSON.
                        - Ensure output is strictly valid JSON.
                        """.trimIndent(), namaIlmiah,
                            Plant::class.java
                        )
                    }
                }catch (_: Exception){
                    continue
                }

                plant.common_names = ecorecord.get(Common_names)
                plant.nama_ilmiah = namaIlmiah
                plant.min_panen = ecorecord.get(MIN_crop_cycle).toInt()
                plant.max_panen = ecorecord.get(MAX_crop_cycle).toInt()
                plant.family = ecorecord.get(Family)
                plant.kategori = ecorecord.get(Category)
                plant.ph = "${ecorecord[O_minimum_ph]}-${ecorecord.get(O_maximum_ph)}"
                plant.temp = "${ecorecord.get(O_minimum_temperature)}-${ecorecord.get(O_maximum_temperature)}"
                writeTaxonomy(plant)
                response.put(i, plant)
            }
        }
        try {
            response.soilCare = soilCare[soilName + resultSoil.pH + "pH"] ?: rag(
                """
                Given the soil type "$soilName" with a pH of ${resultSoil.pH},
                provide a detailed soil care and fertility improvement plan.
            
                Return the result in **valid JSON only** (no explanations, no Markdown).
                
                Schema:
                {
                  "pH_correction": "...",
                  "nutrient_management": {
                    "N": "...",
                    "P": "...",
                    "K": "..."
                  },
                  "organic_matter": "...",
                  "water_retention": "..."
                }
            
                Requirements:
                - All values must be in Indonesian language.
                - Do not add extra fields.
                - Ensure the JSON is strictly valid.
                """.trimIndent(),
                name = soilName + resultSoil.pH + "pH",
                SoilCare::class.java
            )
        }catch (e: Exception){
            log.error("Error while fetching soil care for $soilName with pH ${resultSoil.pH}", e)
        }

        took = (System.currentTimeMillis() - start).toDouble() / 1000000.0
        response.process_time = took
        response.took = took + totalTime
        response.total = total
        response.hashCode = hashCode

        objectMapper.writeValue(result.outputStream, response)
        synchronized(hash.absolutePath.intern()) {
            objectMapper.writeValue(hash, response)
        }
    }

    @GetMapping("/images/{image}")
    fun getImage(@PathVariable image: String): ResponseEntity<StreamingResponseBody> {
        val dir = File("cache/images");
        val file = File(dir, image)
        val canonicalDir = dir.canonicalFile
        val canonicalFile = file.canonicalFile
        if(!canonicalFile.path.startsWith(canonicalDir.path))
            return ResponseEntity.notFound().build()

        val stream = StreamingResponseBody{ output ->
            file.inputStream().use { input ->
                input.copyTo(output)
            }
        }

        val mediaType = when (file.extension.lowercase()) {
            "png" -> MediaType.IMAGE_PNG
            "jpg", "jpeg" -> MediaType.IMAGE_JPEG
            "gif" -> MediaType.IMAGE_GIF
            else -> MediaType.APPLICATION_OCTET_STREAM
        }

        return ResponseEntity
            .ok()
            .contentType(mediaType)
            .body(stream)
    }

    val ignored = mutableSetOf<String>()
    /**
     *
     * @param plant objek yang akan di tulis
     * @param tree plantResponse response
     */
    private fun writeTaxonomy(plant: Plant) {
        plant.genus = plant.nama_ilmiah.split(" ")[0]

        var name = plant.nama_ilmiah.replace(" ssp.", "|").split("|")[0]
        if(ignored.contains(name)){
            log.warn("No taxonomy found for ${plant.nama_ilmiah}")
            return
        }
        val kew = getKew(name)
        if(kew == null) {
            log.warn("No taxonomy found for ${plant.nama_ilmiah}")
            ignored.add(name)
            return
        }
        plant.taxon = "https://powo.science.kew.org/" + kew["url"]?.asText()
        try {
            plant.fullsize = getImagePath(plant, kew = kew)
            plant.thumbnail = getImagePath(plant, kew = kew, size = "thumbnail")
        } catch (e: NullPointerException) {
            log.warn("No image found for ${plant.nama_ilmiah}")
        }
        plant.kingdom = kew["kingdom"].asText()
        plant.family = kew["family"].asText()
    }


    val kewCache = HashMap<String, JsonNode>()
    val kewDir = File("cache/kew_caches");
    init {
        val start = System.nanoTime()
        if(kewDir.mkdirs()) log.info("Kew cache directory created")
        for (it in kewDir.listFiles()) {
            if(!it.name.endsWith(".json")) continue
            var node = objectMapper.readTree(it)

            val totalResults = node["totalResults"]
            totalResults?: run{
                log.warn("No results found for {}", it.name)
                 continue
            }
            if(totalResults.asInt() <= 0) continue
            var flag = false
            for (result in node["results"]) {
                if(result["accepted"].asBoolean()){
                    kewCache[it.name.replace(".json", "")] = result
                    if(debug) log.info("Found accepted resource! author: {}", result["author"]?.asText())
                    flag = true
                    break;
                }
            }
            if(flag) continue
            if(debug) log.info("No accepted resource found for {}",
                 it.name
            )
        }
        val took = (System.nanoTime() - start).toDouble() / 1000000.0
        log.info("KEW caches response loaded in {} ms", took)
    }

    fun getKew(q: String): JsonNode? {
        val file = File(kewDir, "$q.json")
        if(kewCache.containsKey(q) && kewCache[q] != null) {
            return kewCache[q]!!
        }

        val root : JsonNode = if(!file.exists()) {
            val url = "https://powo.science.kew.org/api/1/search?q=$q"
            val response = template.getForEntity(url, JsonNode::class.java)
            synchronized(file.absolutePath.intern()){
                objectMapper.writeValue(file, response.body) }
            response.body
        } else {
            var len = q.length
            var root: JsonNode? = null
            while(len > 1){
                len--
                val q = q.substring(0, len)
                val url = "https://powo.science.kew.org/api/1/search?q=$q"
                val response = template.getForEntity(url, JsonNode::class.java)
                synchronized(file.absolutePath.intern()){
                    objectMapper.writeValue(file, response.body)
                }
                root = response.body
                break;
            }
            root ?: throw NullPointerException("Cant find result of $q")
        }

        if(root["totalResults"].asInt() <= 0) return null;

        for (result in root["results"]) {
            if(result["accepted"].asBoolean()){
                kewCache[q] = result
                if(debug) log.info("Found accepted resource, with author: {}", result["author"]?.asText())
                return result
            }
        }

        if(debug) log.info("No accepted resource found, returning first result. author: {}",
            root["results"][0]["author"].asText())
        return root["results"][0];
    }

    @GetMapping("/loadKEWdataFromDatabase")
    fun loadKEWdataFromDatabase() : String {
        val file = File("cache/kew_caches")
        file.mkdirs()
        val start = System.nanoTime()
        val process = DataHandler.ecocropcsv
        val max = process.size
        var c = file.listFiles().size;
        for(it in process){
            val sciencename = it[Science_name]
            val data = File(file, "$sciencename.json")
            if(data.exists()) continue
            log.info("Processing $sciencename")
            getKew(sciencename)
            c++;
            log.info("Processed $c/$max")
        }
        return "KEW data loaded in ${(System.nanoTime() - start).toDouble() / 1000000.0} ms"
    }

    @JvmOverloads
    @kotlin.jvm.Throws(NullPointerException::class)
    fun getImagePath(plant: Plant? = null, kew: JsonNode? = null, size: String? = "fullsize") : String? {
        val q : String? = plant?.nama_ilmiah

        val dir = File("cache/images")
        dir.mkdirs()
        val file = File(dir, "$q $size.jpg")
        if(file.exists()){
            return file.name
        }
        val url = "http:" +
            if (kew != null) {
                val nodes = kew["images"]
                nodes ?: run {
                    throw NullPointerException("No images found for ${plant?.nama_ilmiah}")
                }
                nodes[0][size].asText()
            } else {
                val kew1 = getKew(q!!)
                kew1?: return null
                val nodes = kew1["images"]
                nodes?: run {
                    throw NullPointerException("No images found for ${plant.nama_ilmiah}")
                }
                nodes[0][size].asText()
            }
        if (!file.exists()) {
            URI.create(url).toURL().openStream().use { inputStream ->
                Files.copy(inputStream, file.toPath())
            }
        }
        return file.name
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
        geo.altitude = elevation.toDouble()
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
        private var debug: Boolean =
            System.getProperty("DEBUG", "false").toBoolean();

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
