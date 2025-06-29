package com.tbear9.plants_api;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.Experimental;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Getter

///  Free plan API (Personal Usage)
///  https://perenual.com/subscription-api-pricing
@Experimental
@Component
@SuppressWarnings({"UNMODIFIABLE_FINAL_FIELD", "JANGAN DI MODIFIKASI, AGAR TIDAK TERJADI EROR"})
public final class PerenualAPI {
    @Setter public static String key = "sk-fh6s685f9f720fc7b11089";
    @Autowired JdbcTemplate template;
    static RestTemplate rest = new RestTemplate();

    public final Map<Integer, JsonNode> plant_list_pages = new HashMap<>(); // PAGE 1 - 405 | SPECIES TANAMAN | TOTAL ADA 10k SPECIES
    public final Map<Integer, JsonNode> plants = new HashMap<>(); // 1 - 10k+ | SPECIES TANAMAN | TOTAL ADA 10k SPECIES
    public final Map<Integer, JsonNode> plant_disease_list_pages = new HashMap<>(); // PAGE 1 - 8 | TANAMAN HAMA | TOTAL ADA 239 SPECIES
    public final Map<Integer, JsonNode> plant_diseases = new HashMap<>(); // PAGE 1 - 8 | TANAMAN HAMA | TOTAL ADA 239 SPECIES
    public final Map<Integer, JsonNode> plant_details = new HashMap<>(); // 1 - 10k+ | DETAIL TANAMAN | TOTAL ADA 10k DETAILS UNTUK SETIAP SPECIES
    public final Map<Integer, JsonNode> plant_guide_pages= new HashMap<>(); // PAGE 1 - 405 | GUIDE TANAMAN | TOTAL ADA 10k PANDUAN UNTUK SETIAP SPECIES
    public final Map<Integer, JsonNode> plant_guides = new HashMap<>(); // 1 - 10k+ | GUIDE TANAMAN | TOTAL ADA 10k PANDUAN UNTUK SETIAP SPECIES
    public final Map<Integer, JsonNode> plant_hardiness = new HashMap<>(); // ID DARI SPECIES | HARDNESS TANAMAN | TOTAL ADA 10k HARDNESS UNTUK SETIAP SPECIES

    public JsonNode getPlantList(int page) throws JsonProcessingException {
        if(!this.plant_list_pages.containsKey(page)){
            JsonNode json = rest.getForObject("https://perenual.com/api/v2/species-list?key="+key+"&page="+page, JsonNode.class);
            String sql = "INSERT INTO plantlist(id, data) VALUES(?, ?)";
            String string = new ObjectMapper().writeValueAsString(json);
            template.update(sql, page, string);
            this.plant_list_pages.put(page, json);
            return json;
        }
        return this.plant_list_pages.get(page);
    }

    public JsonNode getPlantDiseaseList(int page) throws JsonProcessingException {
        if(!this.plant_disease_list_pages.containsKey(page)){
            JsonNode json = rest.getForObject("https://perenual.com/api/pest-disease-list?key="+key+"&page="+page, JsonNode.class);
            String sql = "INSERT INTO plantdiseaselist(id, data) VALUES(?, ?)";
            String string = new ObjectMapper().writeValueAsString(json);
            template.update(sql, page, string);
            this.plant_disease_list_pages.put(page, json);
            return json;
        }
        return this.plant_disease_list_pages.get(page);
    }

    public JsonNode getSpecificPlantDisease(int id) throws JsonProcessingException {
        if(!this.plant_diseases.containsKey(id)){
            for (int i : plant_disease_list_pages.keySet()) {
                JsonNode json = plant_disease_list_pages.get(i);
                JsonNode data = json.path("data");
                for (JsonNode disease : data) {
                    int id1 = disease.path("id").asInt(-1);
                    if(id1 != -1) this.plant_diseases.put(id1, disease);
                    else continue;
                    if(id1 == id) return disease;
                }
            }

            /// Resolving undefined ID
            JsonNode next = plant_disease_list_pages.values().iterator().hasNext()? plant_disease_list_pages.values().iterator().next() : null;
            if(next == null) next = getPlantDiseaseList(1);
            if(next != null) {
                int last_page = next.path("last_page").asInt(-1);
                for (int i = 1; i <= last_page; i++) {
                    if (this.plant_disease_list_pages.containsKey(i)) continue;
                    JsonNode json = getPlantDiseaseList(i);
                    for (JsonNode disease : json.path("data")) {
                        int id1 = disease.path("id").asInt(-1);
                        if(id1 != -1) this.plant_diseases.put(id1, disease);
                        if(id1 == id) return disease;
                    }
                }
            }

            throw new IllegalArgumentException("Species tanaman hama tidak di temukan!");
        }
        return this.plant_diseases.get(id);
    }

    /**
     * @param id ID DARI SPECIES
     * @return TANAMAN (gak detail)
     */
    public JsonNode getSpecificPlant(int id) throws JsonProcessingException {
        if(!this.plants.containsKey(id)){
            Set<Integer> pages = plant_list_pages.keySet();
            for (int i : pages) {
                JsonNode json = plant_list_pages.get(i);
                JsonNode data = json.path("data");
                for (JsonNode plant : data) {
                    int id_ = plant.path("id").asInt(-1);
                    if(id_ != -1) this.plant_details.put(id_, plant);
                    else continue;
                    if(id_ == id) return plant;
                }
            }

            /// Resolving undefined ID
            JsonNode next = plant_list_pages.values().iterator().hasNext()? plant_list_pages.values().iterator().next() : null;
            if(next == null) next = getPlantList(1);
            if(next != null) {
                int last_page = next.path("last_page").asInt(-1);
                for (int i = 1; i <= last_page; i++) {
                    if (pages.contains(i)) continue;
                    JsonNode json = getPlantList(i);
                    for (JsonNode plant : json.path("data")) {
                        int id_ = plant.path("id").asInt(-1);
                        if(id_ != -1) this.plant_details.put(id_, plant);
                        if(id_ == id) return plant;
                    }
                }
            }

            throw new IllegalArgumentException("ID TANAMAN TIDAK DITEMUKAN");
        }
        return this.plant_details.get(id);
    }

    @SuppressWarnings({"Untuk sekarang jangan di gunakan jika memang tidak perlu",
            "Cost = 1 request per unique id", "limit anggaran key request"})
    public JsonNode getSpecificPlantDetails(int id) throws JsonProcessingException {
        if(!this.plant_details.containsKey(id)){
            JsonNode json = rest.getForObject("https://perenual.com/api/v2/species/details/"+id+"?key="+key, JsonNode.class);
            String sql = "INSERT INTO plantdetails(id, data) VALUES(?, ?)";
            String string = new ObjectMapper().writeValueAsString(json);
            template.update(sql, id, string);
            this.plant_details.put(id, json);
            return json;
        }
        return this.plant_details.get(id);
    }

    public JsonNode getPlantGuide(int page) throws JsonProcessingException {
        if(this.plant_guide_pages.containsKey(page)){
            return this.plant_guide_pages.get(page);
        }
        JsonNode json = rest.getForObject("https://perenual.com/api/species-care-guide-list?key=" + key + "&page=" + page, JsonNode.class);
        String sql = "INSERT INTO plantguidelist(id, data) VALUES(?, ?)";
        String string = new ObjectMapper().writeValueAsString(json);
        template.update(sql, page, string);
        this.plant_guide_pages.put(page, json);
        return json;
    }

    /**
     * @param id ID DARI SPECIES
     * @return GUIDE TANAMAN
     */
    public JsonNode getSpecificPlantGuide(int id) throws JsonProcessingException {
        if(!this.plant_guides.containsKey(id)){
            /// Searching fasdasasghasjbg
            for (int i : plant_guide_pages.keySet()) {
                JsonNode json = plant_guide_pages.get(i);
                JsonNode data = json.path("data");
                for (JsonNode guide : data) {
                    int id_ = guide.path("id").asInt(-1);
                    if(id_ != -1) this.plant_guides.put(id_, guide);
                    else continue;
                    if(id_ == id) return guide;
                }
            }

            /// Resolving undefined page
            JsonNode next = plant_guide_pages.values().iterator().hasNext()? plant_guide_pages.values().iterator().next() : null;
            if(next == null) next = getPlantGuide(1);
            if(next != null) {
                int last_page = next.path("last_page").asInt(-1);
                for (int i = 1; i <= last_page; i++) { /// i = page
                    if (plant_guide_pages.containsKey(i)) continue;
                    JsonNode json = getPlantGuide(i);
                    for (JsonNode guide : json.path("data")) {
                        int id_ = guide.path("species_id").asInt(-1);
                        if(id_ != -1) this.plant_guides.put(id_, guide);
                        if(id_ == id) return guide;
                    }
                }
            }

            /// if not found throw this exception
            throw new IllegalArgumentException("GUIDE TANAMAN TIDAK DITEMUKAN");
        }
        return this.plant_guides.get(id);
    }

    @SuppressWarnings({"Untuk sekarang jangan di gunakan jika memang tidak perlu",
            "Cost = 1 request per unique id", "limit anggaran key request"})
    public JsonNode getPlantHardiness(int id) throws JsonProcessingException {
        if(this.plant_hardiness.containsKey(id)){
            JsonNode json = rest.getForObject("https://perenual.com/api/hardiness-map?species_id="+id+"&key="+key, JsonNode.class);
            String sql = "INSERT INTO planthardiness(id, data) VALUES(?, ?)";
            String string = new ObjectMapper().writeValueAsString(json);
            template.update(sql, id, string);
            this.plant_hardiness.put(id, json);
            return json;
        }
        return this.plant_hardiness.get(id);
    }
}