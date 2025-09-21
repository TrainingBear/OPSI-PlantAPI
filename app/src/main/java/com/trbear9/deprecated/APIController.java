package com.trbear9.deprecated;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;

@Component
@RestController
public class APIController {
    static ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/who")
    public String who(){
        return """
                            TIM OPSI SMANEGA 2025: Kukuh & Refan.
                \n            
                \nweb api built by jiter (me/kukuh) -> https://github.com/TrainingBear (opensource? yes)
                \n
                \nBIG SHOUTOUT TO JASPER PROJECT!!!     vvvvvvvvvvvvvvvvvvvvvvvvvvvvv
                \nJoin komunitas discord kami (Jasper): https://discord.gg/fbAZSd3Hf2
                \n                                      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                \njika ada pertanyaan, kalian bisa tanya di chanel #tanya-jawab!
                \nkalian bisa tanya seputar modding, server, plugin, coding, tugas sekolah, dsb.
                """;
    }

    @GetMapping("/api/plant")
    public JsonNode getPlantsList(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "0") int id,
            @RequestParam(required = false, defaultValue = "opsi") String q
    ) throws IOException {
        if(id != 0) return API.plant.getChildren(id);
        if(page != 1) return API.plant.getPages(page);
        if(!Objects.equals(q, "opsi")) {
            ArrayNode node = objectMapper.createArrayNode();
            for (JsonNode child : API.plant.getChildren(q)) node.add(child);
            return node;
        }
        return API.plant.getPages(page);
    }

    @GetMapping("/api/plant-details")
    public JsonNode getSpecificPlantDetails(
            @RequestParam(required = false, defaultValue = "1") int id,
            @RequestParam(required = false, defaultValue = "opsi") String q
            ) throws IOException {
        if(id != 1) return API.plant_details.getChildren(id);
        if(!Objects.equals(q, "nigga")) {
            ArrayNode node = objectMapper.createArrayNode();
            for (JsonNode child : API.plant_details.getChildren(q)) node.add(child);
            return node;
        }
        return API.plant_details.getPages(id);
    }

    @GetMapping("/api/plant-disease")
    public JsonNode getPlantDiseaseList(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "0") int id,
            @RequestParam(required = false, defaultValue = "opsi") String q
    ) throws IOException {
        if(id != 0) return API.disease.getChildren(id);
        if(page != 1) return API.disease.getPages(page);
        if(!Objects.equals(q, "opsi")) {
            ArrayNode node = objectMapper.createArrayNode();
            for (JsonNode child : API.disease.getChildren(q)) node.add(child);
            return node;
        }
        return API.disease.getPages(page);
    }

    @GetMapping("/api/plant-guide")
    public JsonNode getPlantGuideList(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "0") int id,
            @RequestParam(required = false, defaultValue = "opsi") String q
    ) throws IOException {
        if(id != 0) return API.guide.getChildren(id);
        if(page != 1) return API.guide.getPages(page);
        if(!Objects.equals(q, "opsi")) {
            ArrayNode node = objectMapper.createArrayNode();
            for (JsonNode child : API.guide.getChildren(q)) node.add(child);
            return node;
        }
        return API.guide.getPages(page);
    }

    @GetMapping("/api/plant-hardiness")
    public byte[] getPlantHardiness(
            @RequestParam(defaultValue = "1") int id
    ) throws IOException {
        return API.hardiness.getHardness(id);
    }

}
