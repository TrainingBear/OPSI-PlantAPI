package com.trbear9.plants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trbear9.plants.api.Response;
import com.trbear9.plants.api.SoilParameters;
import com.trbear9.plants.api.UserVariable;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.*;

@Service
@RestController
public class Poster {
    public static final String key = System.getenv("OPEN_AI_KEY");
    @Autowired private static RestTemplate template;
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/who")
    public String who(){
        return """
                            TIM OPSI SMANEGA 2025: Kukuh & Refan.
                \n            
                \nweb api built by jiter (me/kukuh) -> https://github.com/TrainingBear (opensource? yes)
                \n
                \nBIG SHOUTOUT TO JASPER PROJECT!!!     vvvvvvvvvvvvvvvvvvvvvvvvvvvvv
                \nJoin komunitas discord kami (Jasper): https://discord.gg/fbAZSd
                \n                \n""";
    }


    @PostMapping("/predict")
    public Response postVar(@RequestBody UserVariable data) throws JsonProcessingException {
        byte[] image = data.getImage();
        float[] prediction = FAService.predict(image);
        int max = FAService.argmax(prediction);
        SoilParameters soil = FAService.soil[max];
        String soilName = FAService.label[max];
        data.modify(soil);

        Map<Integer, Set<CSVRecord>> result = DB.ecoCropDB_csv(data);
        Response response = Response.builder().soilName(soilName).build();
        for (int i : result.keySet())
            for (CSVRecord ecorecord : result.get(i)) {
                CSVRecord perawatanrecord = DB.perawatan_csv(ecorecord);
                String nama_ilmiah = ecorecord.get(E.Science_name);
                StringBuilder querry;
                if(perawatanrecord != null){
                    querry = new StringBuilder("generate a plants guide & car, that include watering, pruning, fertilization, sunlight, pest management, the common name in indonesia, and level of difficulty of care. for this specified plant \n");
                    String perawatan = perawatanrecord.get(E.PERAWATAN);
                    String penyakit = perawatanrecord.get(E.PENYAKIT);
                    String nama_tanaman = perawatanrecord.get(E.NAME);
                    querry.append("Nama ilmiah: ").append(nama_ilmiah).append("\n");
                    querry.append("Nama tanaman: ").append(nama_tanaman).append("\n");
                    querry.append("Perawatan: ").append(perawatan).append("\n");
                    querry.append("Penyakit: ").append(penyakit).append("\n");
                }
                else {
                    querry = new StringBuilder("generate a plants guide & care, that include watering, pruning, fertilization, sunlight, pest management, the common name, and level of difficulty of care. for this plant ");
                    querry.append(nama_ilmiah).append('\n');
                }
                querry.append("generate them in JSON, with format {plant_care:\"response\", difficulty:EASY or MEDIUM or HARD, common_name: \"the common name in indonesia\", prune_guide: \"the youtube/blog link that refer prune method for this plant\"}").append('\n');
                querry.append("generate the output in indonesian language");
                String respon = rag(querry.toString());
                response.put(i, Map.of(nama_ilmiah, respon));
                File file = new File("open_ai/responses", System.nanoTime()+".json");
                file.mkdirs();
                try {
                    objectMapper.writeValue(file, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        return response;
    }

    private String rag(String input) throws JsonProcessingException {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.setBearerAuth(key);

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        body.put("model", "o4-mini-deep-research");
        message.put("role", "user");
        message.put("content", input);
        body.put("input", List.of(message));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, header);
        ResponseEntity<String> response = template.postForEntity("https://api.openai.com/v1/responses", request, String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        return root.get("output").get(0).get("content").get(0).get("text").asText();
    }
}
