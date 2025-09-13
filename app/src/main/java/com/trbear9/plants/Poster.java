package com.trbear9.plants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import com.trbear9.plants.api.Response;
import com.trbear9.plants.api.SoilParameters;
import com.trbear9.plants.api.UserVariable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RestController
@Getter
public class Poster {
    static private final SimpleClientHttpRequestFactory factory;
    static private final RestTemplate template;
    static {
        factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMinutes(5));
        factory.setReadTimeout(Duration.ofMinutes(5));
        template = new RestTemplate(factory);
    }
    public static final String key = System.getenv("OPEN_AI_KEY");
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private static final String gist_id = "84d0e105aaabce26c8dfbaff74b2280e";
    private static final String git_token = System.getenv("GITHUB_TOKEN");
    static NgrokClient ngrokClient;
    static Tunnel tunnel;
    private static long startTime = -1;

    public static String getUrl() throws JsonProcessingException {
        if(tunnel != null)
            return tunnel.getPublicUrl();

        String gist = "https://gist.githubusercontent.com/TrainingBear/84d0e105aaabce26c8dfbaff74b2280e/raw/url.json";
        ResponseEntity<String> response = template.getForEntity(gist, String.class);
        JsonNode json = objectMapper.readTree(response.getBody());
        return json.get("content").asText();
    }

    public static void start(){
        if(ngrokClient==null) {
            ngrokClient = new NgrokClient.Builder().build();
            CreateTunnel address = new CreateTunnel.Builder()
                    .withAddr(8080)
                    .withProto(Proto.HTTP)
                    .build();
            tunnel = ngrokClient.connect(address);
            String publicUrl = tunnel.getPublicUrl();
            startTime = System.currentTimeMillis();
            log.info("ngrok tunnel \"{}\" -> \"{}\"", tunnel.getName(), publicUrl);

            Map<String, String> content = new HashMap<>();
            content.put("content",
                    "{" +
                        "\"content\": \"" + publicUrl + "\"," +
                        "\"started\": " + Poster.startTime + "," +
                        "\"stopped\": " + "\"N/A\"" +
                    "}");
            Map<String, Object> json = new HashMap<>();
            json.put("url.json", content);

            Map<String, Object> file = new HashMap<>();
            file.put("files", json);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(git_token);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(file, headers);

            ResponseEntity<String> response =
                    template.postForEntity
                            ("https://api.github.com/gists/" + gist_id, request, String.class);
            if(response.getStatusCode().is2xxSuccessful()){
                log.info("Gist updated: {}", response.getBody());
            } else {
                log.error("Gist update failed: {}", response.getBody());
            }
        }
    }

    public static void stop(){
        if(tunnel != null && ngrokClient != null) {
            ngrokClient.disconnect(tunnel.getPublicUrl());
            tunnel = null;
            ngrokClient.kill();
            ngrokClient = null;

            Map<String, String> content = new HashMap<>();
            content.put("content",
                    "{" +
                            "\"content\": \"http://localhost:8080\"," +
                            "\"started\": " + Poster.startTime + "," +
                            "\"stopped\": " + System.currentTimeMillis() + "," +
                            "}");
            Map<String, Object> json = new HashMap<>();
            json.put("url.json", content);
            Map<String, Object> file = new HashMap<>();
            file.put("files", json);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(git_token);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(file, headers);

            ResponseEntity<String> response =
                    template.postForEntity
                            ("https://api.github.com/gists/" + gist_id, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Gist updated: {}", response.getBody());
            } else {
                log.error("Gist update failed: {}", response.getBody());
            }
        }
    }

    @GetMapping("/who")
    public String who(){
        return """
                            \n \n \n
                            TIM OPSI SMANEGA 2025
                            oleh Kukuh & Refan.
                RestAPI has been built by kujatic (trbear) -> https://github.com/TrainingBear (opensource)
                BIG SHOUTOUT TO JASPER                vvvvvvvvvvvvvvvvvvvvvvvvv
                Join komunitas discord kami (Jasper): https://discord.gg/fbAZSd3Hf2
                \n \n \n
                """;
    }


    @PostMapping("/predict")
    public String postVar(@RequestBody UserVariable data) throws IOException {
        log.info("POST /predict");
        byte[] image = data.getImage();
        float[] prediction = FAService.predict(image);
        int max = FAService.argmax(prediction);
        SoilParameters soil = FAService.soil[max];
        String soilName = FAService.label[max];
        log.info("Soil: {}", soilName);
        data.modify(soil);

        Map<Integer, Set<CSVRecord>> result = DB.ecoCropDB_csv(data);
        Response response = new Response();
        response.setSoilName(soilName);
        for (int i : result.keySet())
            for (CSVRecord ecorecord : result.get(i)) {
                CSVRecord perawatanrecord = DB.perawatan_csv(ecorecord);
                String nama_ilmiah = ecorecord.get(E.Science_name);

                File dir = new File("open_ai/responses");
                if (dir.mkdirs())
                    log.info("Directory created: {}", dir.getAbsolutePath());
                File file = new File(dir, nama_ilmiah + ".json");
                if (!file.exists()) {
                    StringBuilder query;
                    query = new StringBuilder("generate a plants guide & care, that include watering, pruning, fertilization, sunlight, pest management, the common name, and level of difficulty of care. for this plant ");
                    if (perawatanrecord != null) {
                        String perawatan = perawatanrecord.get(E.PERAWATAN);
                        String penyakit = perawatanrecord.get(E.PENYAKIT);
                        String nama_tanaman = perawatanrecord.get(E.NAME);
                        query.append("Nama ilmiah: ").append(nama_ilmiah).append("\n");
                        query.append("Nama tanaman: ").append(nama_tanaman).append("\n");
                        query.append("Perawatan: ").append(perawatan).append("\n");
                        query.append("Penyakit: ").append(penyakit).append("\n");
                    } else {
                        query.append(nama_ilmiah).append('\n');
                    }
                    query.append("generate them in JSON, with format {plant_care:\"response\", difficulty:EASY or MEDIUM or HARD, common_name: \"the common name in indonesia\", prune_guide: \"the youtube/blog link that refer prune method for this plant or either just say this plant cant be pruned\"}").append('\n');
                    query.append("generate the output in indonesian language");
                    String respon = rag(query.toString());
                    JsonNode node = objectMapper.readTree(respon);
                    objectMapper.writeValue(file, respon);
                    node = node.get("output").get(1).get("content").get(0).get("text");
                    response.put(i, nama_ilmiah, node.asText());
                } else{
                    JsonNode node = objectMapper.readTree(file);
                    node = objectMapper.readTree(node.asText());
                    node = node.get("output").get(1).get("content").get(0).get("text");
                    response.put(i, nama_ilmiah, node.asText());
                }
            }
        File dir = new File("open_ai/cache");
        dir.mkdirs();
        File file = new File(dir, System.nanoTime()+".json");
        String value = objectMapper.writeValueAsString(response);
        objectMapper.writeValue(file, value);
        return value;
    }

    private String rag(String input) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.setBearerAuth(key);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "o3");
        body.put("input", input);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, header);
//        int attempt = 0;
        return template.postForEntity("https://api.openai.com/v1/responses", request, String.class).getBody();
//        while (true) {
//            try {
//                return template.postForEntity("https://api.openai.com/v1/responses", request, String.class).getBody();
//            } catch (HttpClientErrorException.TooManyRequests e) {
//                e.printStackTrace();
//                attempt++;
//                if (attempt > 7) {
//                    throw new RuntimeException("Exceeded max retries after hitting rate limits", e);
//                }
//
//                // Check if OpenAI returned Retry-After header
//                String retryAfterHeader = e.getResponseHeaders() != null ?
//                        e.getResponseHeaders().getFirst("Retry-After") : null;
//
//                long sleepMillis;
//                if (retryAfterHeader != null) {
//                    // OpenAI sometimes suggests how long to wait
//                    sleepMillis = Long.parseLong(retryAfterHeader) * 1000L;
//                } else {
//                    // Fallback: exponential backoff
//                    sleepMillis = (long) Math.pow(2, attempt) * 1000L;
//                }
//
//                System.err.printf("Rate limit hit (429). Retrying in %d ms (attempt %d/%d)%n",
//                        sleepMillis, attempt, 7);
//
//                try {
//                    TimeUnit.MILLISECONDS.sleep(sleepMillis);
//                } catch (InterruptedException ex) {
//                    Thread.currentThread().interrupt();
//                    throw new RuntimeException("Retry interrupted", ex);
//                }
//            }
//        }
    }
}
