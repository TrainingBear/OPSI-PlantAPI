package com.trbear9.plants

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.core.StreamWriteConstraints
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.trbear9.plants.api.GeoParameters
import com.trbear9.plants.api.UserVariable
import com.trbear9.plants.api.blob.Plant
import com.trbear9.plants.api.blob.SoilCare
import org.apache.commons.csv.CSVRecord
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.Duration

class Migration {
    val log = LoggerFactory.getLogger(Migration::class.java)
    val factory: SimpleClientHttpRequestFactory = SimpleClientHttpRequestFactory()
    var template = RestTemplate()
    val objectMapper = ObjectMapper()
    init {
        factory.setConnectTimeout(Duration.ofHours(1))
        factory.setReadTimeout(Duration.ofHours(1))
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

    @Test
    fun migrateImage(){
        val fullsize = File("cache/fullsize")
        val thumbnail = File("cache/thumbnail")
        fullsize.mkdirs()
        thumbnail.mkdirs()

        val main = File("cache/images")
        val files = main.listFiles()
        log.info(files.size.toString())
        for (file in files) {
            if(file.name.endsWith("fullsize.jpg")){
                val target = File(fullsize, file.name.replace(" fullsize", ""))
                if(!target.exists())
                    Files.move(file.toPath(), target.toPath())
            }
            else {
                val target = File(thumbnail, file.name.replace(" thumbnail", ""))
                if(!target.exists()) Files.move(
                    file.toPath(),
                    target.toPath()
                )
            }
        }
    }

    @Test
    fun generateSoil() {
        throw RuntimeException("Stub!")
        FastApiService.label.forEach { label ->
            for(i in 0..28) {
                val pH = i * 0.5f
                log.info("Generating soil for $label with pH $pH")
                rag(
                    """
                Given the soil type "$label" with a pH of ${pH},
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
                    name = label +" "+ pH,
                    SoilCare::class.java
                )
            }
        }
    }

    @Test
    fun loadPlant() {
        val plants = mutableSetOf<CSVRecord>()
        var iteration = 0;
        for(lat in -11 .. 6) {
            val geo = GeoParameters().apply {
                latitude = lat.toDouble()
                min = 18.0
                max = 32.0
            }
            for(rain in 1..5){
                geo.rainfall = rain * 1000.0
                val variable = UserVariable()
                variable.geo = geo
                val processed = DataHandler.process(variable)
                for (score in processed.keys) {
                    for (record in processed[score]!!) {
                        plants.add(record)
//                        log.info("${++iteration} Added {} with score {}", DataHandler.getScienceName(record), score)
                    }
                }
            }
        }
        log.info("Loaded plants with size of: ${plants.size}")
        val loaded = mutableSetOf<String>()
        val initialized = mutableSetOf<String>()

        for (record in plants) {
            val sname = DataHandler.getScienceName(record)
            loaded.add(sname!!)
        }
        val dirrr = File("cache/plant")
        for (file in dirrr.listFiles()) {
            val name = file.name.replace(".json", "")
            initialized.add(name)
        }

        val missing = loaded - initialized
        log.info("Missing plants: ${missing.size}")

        for (record in plants) {
            val name = DataHandler.getScienceName(record)
            log.info("Processing $name")
            rag(
                """
                        You are given a plant with the scientific name: "$name".
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
                        """.trimIndent(), name!!,
                Plant::class.java
            )
        }
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

    val ignored = mutableSetOf<String>()
    /**
     *
     * @param plant objek yang akan di tulis
     * @param tree plantResponse response
     */
    private fun writeTaxonomy(plant: Plant) {
        plant.genus = plant.nama_ilmiah.split(" ")[0]

        val name =
            if(plant.nama_ilmiah.contains(" ssp."))
            plant.nama_ilmiah.replace(" ssp.", "|").split("|")[0]
        else if (plant.nama_ilmiah.contains(" var."))
            plant.nama_ilmiah.replace(" var.", "|").split("|")[0]
        else if(plant.nama_ilmiah.contains(" sp."))
            plant.nama_ilmiah.replace(" sp.", "|").split("|")[0]
        else
            plant.nama_ilmiah

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
        val kew_cache_blob = File("cache/kew_cache_blob")
        kew_cache_blob.mkdirs()
        for (it in kewDir.listFiles()) {
            if(!it.name.endsWith(".json")) continue
            var node = objectMapper.readTree(it)

            var totalResults = node["totalResults"]
            totalResults?: run {
                log.warn("No results found for {}", it.name)
                getKew(it.name)
                var nama_ilmiah = it.name.replace(".json", "")
                nama_ilmiah = if (nama_ilmiah.contains(" ssp."))
                    nama_ilmiah.replace(" ssp.", "|").split("|")[0]
                else if (nama_ilmiah.contains(" var."))
                    nama_ilmiah.replace(" var.", "|").split("|")[0]
                else if (nama_ilmiah.contains(" sp."))
                    nama_ilmiah.replace(" sp.", "|").split("|")[0]
                else
                    nama_ilmiah
                node = getKew(nama_ilmiah)
                totalResults = node["totalResults"]
                if (node == null || node["totalResults"] == null) continue
            }
            if(totalResults.asInt() <= 0) continue
            var flag = false
            for (result in node["results"]) {
                if(result["accepted"].asBoolean()){
                    kewCache[it.name.replace(".json", "")] = result
                    log.info("Found accepted resource! author: {}", result["author"]?.asText())
                    flag = true
                    break;
                }
            }
            if(flag) continue
            log.info("No accepted resource found for {}",
                 it.name
            )
            val file = File(kew_cache_blob, it.name)
            if(file.exists()) continue
            objectMapper.writeValue(file, node)
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
                log.info("Found accepted resource, with author: {}", result["author"]?.asText())
                return result
            }
        }

        log.info("No accepted resource found, returning first result. author: {}",
            root["results"][0]["author"].asText())
        return root["results"][0];
    }


    val plantDir = File("cache/plant")
    val soilDir = File("cache/soil")

    private fun <T> rag(input: String?, name: String, clazz: Class<T>) {
        if(clazz == Plant::class.java) {
            val file = File(plantDir, "$name.json")
            var tree: JsonNode? = null
            if(file.exists()){
                log.info("$name already exists")
            }else {

                val header = org.springframework.http.HttpHeaders()
                header.contentType = MediaType.APPLICATION_JSON
                header.setBearerAuth(System.getenv("OPEN_AI_KEY"))

                val body: MutableMap<String?, Any?> = HashMap()
                body.put("model", "o4-mini")
                body.put("input", input)

                val request = HttpEntity(body, header)

                var respon: String?
                try {
                    respon = template.postForEntity(
                        "https://api.openai.com/v1/responses",
                        request,
                        String::class.java
                    ).body
                } catch (e: Exception) {
                    log.error("Something wrong when getting $name from OpenAI")
                    throw e
                }
                tree = objectMapper.readTree(respon)
                synchronized(file.absolutePath.intern()) {
                    objectMapper.writeValue(file, tree)
                }
            }
            var dir = File("cache/plant_blob")
            dir.mkdirs()
            dir = File(dir, "$name.json")
            if(!dir.exists()) {
                if(tree == null){
                    tree = objectMapper.readTree(file)
                }
                val plant: Plant
                try{
                    tree = objectMapper.readTree(sanitize(tree["output"][1]["content"][0]["text"].asText()))
                    plant = objectMapper.treeToValue(tree, Plant::class.java)
                } catch (_: UnrecognizedPropertyException){
                    log.error("Something wrong when parsing $name")
                    file.delete()
                    return
                } catch (_: JsonParseException){
                    log.error("Something wrong when parsing $name")
                    file.delete()
                    return
                }
                plant.nama_ilmiah = name
                writeTaxonomy(plant)
                objectMapper.writeValue(dir, plant)
            }
        } else if(clazz == SoilCare::class.java) {
            val file = File(soilDir, "$name.json")
            var tree: JsonNode? = null
            if(file.exists()) {
                log.info("$name already exists")
            } else {

                val header = org.springframework.http.HttpHeaders()
                header.contentType = MediaType.APPLICATION_JSON
                header.setBearerAuth(System.getenv("OPEN_AI_KEY"))

                val body: MutableMap<String?, Any?> = HashMap()
                body.put("model", "o4-mini")
                body.put("input", input)

                val request = HttpEntity(body, header)
                var respon: String?
                try {
                    respon = template.postForEntity(
                        "https://api.openai.com/v1/responses",
                        request,
                        String::class.java
                    ).body
                } catch (e: Exception) {
                    log.error("Something wrong when getting $name from OpenAI")
                    throw e
                }
                tree = objectMapper.readTree(respon)
                synchronized(file.absolutePath.intern()) {
                    objectMapper.writeValue(file, tree)
                }
            }
            var dir = File("cache/soil_blob")
            dir.mkdirs()
            dir = File(dir, "$name.json")
            if(!dir.exists() && tree != null) {
                tree = objectMapper.readTree(sanitize(tree["output"][1]["content"][0]["text"].asText()))
                val soil = objectMapper.treeToValue(tree, SoilCare::class.java)
                objectMapper.writeValue(dir, soil)
            }
        }
        else throw IllegalArgumentException("Invalid class")
    }

    fun sanitize(text: String): String {
        return text
            .replace(Regex("^```(json)?\\s*"), "")
            .replace(Regex("```$"), "")
            .replace(Regex("\",\"}"), "\"}")
            .trim()
    }

}