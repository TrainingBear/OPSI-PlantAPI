package com.trbear9.plants_api.code_lama;

public class Perenualapi {
    //    private TabList create(Table table, boolean hasParent){
//        return new TabList(template, table, hasParent){};
//    }
//
//    TabList plant;
//    TabList guide;
//    TabList disease;
//    TabList hardiness;
//    TabList plant_details;
//    TabList[] content_as_array;
//
//    @PostConstruct
//    public void init() throws JsonProcessingException {
//        plant = create(Table.PLANT_LIST, true);
//        guide = create(Table.PLANT_GUIDE_LIST, true);
//        disease = create(Table.PLANT_DISEASE_LIST, true);
//        hardiness = create(Table.PLANT_HARDINESS, false);
//        plant_details = create(Table.PLANT_DETAILS, false);
//        content_as_array = new TabList[]{plant, guide, disease, hardiness, plant_details};
//    }

//    public final Map<Integer, JsonNode> plant_list_pages = new HashMap<>(); // PAGE 1 - 405 | SPECIES TANAMAN | TOTAL ADA 10k SPECIES
//    public final Map<Integer, JsonNode> plant_disease_list_pages = new HashMap<>(); // PAGE 1 - 8 | TANAMAN HAMA | TOTAL ADA 239 SPECIES
//    public final Map<Integer, JsonNode> plant_guide_pages= new HashMap<>(); // PAGE 1 - 405 | GUIDE TANAMAN | TOTAL ADA 10k PANDUAN UNTUK SETIAP SPECIES
//    public final Map<Integer, JsonNode> plant_hardiness = new HashMap<>(); // ID DARI SPECIES | HARDNESS TANAMAN | TOTAL ADA 10k HARDNESS UNTUK SETIAP SPECIES
//    public final Map<Integer, JsonNode> plant_details = new HashMap<>(); // 1 - 10k+ | DETAIL TANAMAN | TOTAL ADA 10k DETAILS UNTUK SETIAP SPECIES
//
//    /// data yang tidak langsung terkait dengan database
//    public final Map<Integer, JsonNode> plants = new HashMap<>(); // 1 - 10k+ | SPECIES TANAMAN | TOTAL ADA 10k SPECIES
//    public final Map<Integer, JsonNode> plant_diseases = new HashMap<>(); // PAGE 1 - 8 | TANAMAN HAMA | TOTAL ADA 239 SPECIES
//    public final Map<Integer, JsonNode> plant_guides = new HashMap<>(); // 1 - 10k+ | GUIDE TANAMAN | TOTAL ADA 10k PANDUAN UNTUK SETIAP SPECIES

//    public void update(Table table, int id, JsonNode json) throws JsonProcessingException {
//        String sql = "INSERT INTO "+table.name+"(id, data) VALUES(?, ?)";
//        String string = mapper.writeValueAsString(json);
//        template.update(sql, id, string);
//        this.plant_list_pages.put(id, json);
//    }
//
//    public JsonNode getPlantList(int page) throws JsonProcessingException {
//        if(!this.plant_list_pages.containsKey(page)){
//            JsonNode json = rest.getForObject("https://perenual.com/api/v2/species-list?key="+key+"&page="+page, JsonNode.class);
//            update(Table.PLANT_LIST, page, json);
//            return json;
//        }
//        return this.plant_list_pages.get(page);
//    }
//
//    public JsonNode getSpeciesPage(Table table, int id) throws JsonProcessingException {
//        return switch (table){
//            case PLANT_LIST -> {
//                yield getPlantList(id);
//            }
//            case PLANT_DISEASE_LIST -> {
//                yield getPlantDiseaseList(id);
//            }
//            case PLANT_DETAILS -> {
//                yield getSpecificPlantDetails(id);
//            }
//            case PLANT_GUIDE_LIST -> {
//                yield getPlantGuide(id);
//            }
//            case PLANT_GUIDE_DETAILS -> {
//                yield getSpecificPlantGuide(id);
//            }
//            case PLANT_HARDINESS -> {
//                yield getPlantHardiness(id);
//            }
//        };
//    }
//
//    public JsonNode getPlantDiseaseList(int page) throws JsonProcessingException {
//        if(!this.plant_disease_list_pages.containsKey(page)){
//            JsonNode json = rest.getForObject("https://perenual.com/api/pest-disease-list?key="+key+"&page="+page, JsonNode.class);
//            update(Table.PLANT_DISEASE_LIST, page, json);
//            return json;
//        }
//        return this.plant_disease_list_pages.get(page);
//    }
//
//    public JsonNode exploreStrandedSpecies(Map<Integer, JsonNode> pages, int id){
//        JsonNode next = pages.values().iterator().hasNext()? pages.values().iterator().next() : null;
//        if(next == null) next = getPlantDiseaseList(1);
//        if(next != null) {
//            int last_page = next.path("last_page").asInt(-1);
//            for (int i = 1; i <= last_page; i++) {
//                if (this.plant_disease_list_pages.containsKey(i)) continue;
//                JsonNode json = getPlantDiseaseList(i);
//                for (JsonNode disease : json.path("data")) {
//                    int id1 = disease.path("id").asInt(-1);
//                    if(id1 != -1) this.plant_diseases.put(id1, disease);
//                    if(id1 == id) return disease;
//                }
//            }
//        }
//    }
//
//    public JsonNode getSpecificPlantDisease(int id) throws JsonProcessingException {
//        if(!this.plant_diseases.containsKey(id)){
//            for (int i : plant_disease_list_pages.keySet()) {
//                JsonNode json = plant_disease_list_pages.get(i);
//                JsonNode data = json.path("data");
//                for (JsonNode disease : data) {
//                    int id1 = disease.path("id").asInt(-1);
//                    if(id1 != -1) this.plant_diseases.put(id1, disease);
//                    else continue;
//                    if(id1 == id) return disease;
//                }
//            }
//
//            /// Resolving undefined ID
//
//
//            throw new IllegalArgumentException("Species tanaman hama tidak di temukan!");
//        }
//        return this.plant_diseases.get(id);
//    }
//
//    public JsonNode getSpecificPlantDisease(String q) throws JsonProcessingException {
//        for (JsonNode value : plant_diseases.values()) {
//            if(value.path("common_name").asText().contains(q)) return value;
//        }
//        for (JsonNode value : plant_diseases.values()) {
//            if(value.path("scientific_name").asText().contains(q)) return value;
//        }
//
//        throw new IllegalArgumentException("Species tanaman hama tidak di temukan!");
//    }
//
//    /**
//     * @param id ID DARI SPECIES
//     * @return TANAMAN (gak detail)
//     */
//    public JsonNode getSpecificPlant(int id) throws JsonProcessingException {
//        if(!this.plants.containsKey(id)){
//            Set<Integer> pages = plant_list_pages.keySet();
//            for (int i : pages) {
//                JsonNode json = plant_list_pages.get(i);
//                JsonNode data = json.path("data");
//                for (JsonNode plant : data) {
//                    int id_ = plant.path("id").asInt(-1);
//                    if(id_ != -1) this.plants.put(id_, plant);
//                    else continue;
//                    if(id_ == id) return plant;
//                }
//            }
//
//            /// Resolving undefined ID
//            JsonNode next = plant_list_pages.values().iterator().hasNext()? plant_list_pages.values().iterator().next() : null;
//            if(next == null) next = getPlantList(1);
//            if(next != null) {
//                int last_page = next.path("last_page").asInt(-1);
//                for (int i = 1; i <= last_page; i++) {
//                    if (pages.contains(i)) continue;
//                    JsonNode json = getPlantList(i);
//                    for (JsonNode plant : json.path("data")) {
//                        int id_ = plant.path("id").asInt(-1);
//                        if(id_ != -1) this.plant_details.put(id_, plant);
//                        if(id_ == id) return plant;
//                    }
//                }
//            }
//
//            throw new IllegalArgumentException("ID TANAMAN TIDAK DITEMUKAN");
//        }
//        return this.plants.get(id);
//    }
//
//    @SuppressWarnings({"Untuk sekarang jangan di gunakan jika memang tidak perlu",
//            "Cost = 1 request per unique id", "limit anggaran key request"})
//    public JsonNode getSpecificPlantDetails(int id) throws JsonProcessingException {
//        if(!this.plant_details.containsKey(id)){
//            JsonNode json = rest.getForObject("https://perenual.com/api/v2/species/details/"+id+"?key="+key, JsonNode.class);
//            update(Table.PLANT_DETAILS, id, json);
//            return json;
//        }
//        return this.plant_details.get(id);
//    }
//
//    public JsonNode getPlantGuide(int page) throws JsonProcessingException {
//        if(this.plant_guide_pages.containsKey(page)){
//            return this.plant_guide_pages.get(page);
//        }
//        JsonNode json = rest.getForObject("https://perenual.com/api/species-care-guide-list?key=" + key + "&page=" + page, JsonNode.class);
//        update(Table.PLANT_GUIDE_LIST, page, json);
//        return json;
//    }
//
//    /**
//     * @param id ID DARI SPECIES
//     * @return GUIDE TANAMAN
//     */
//    public JsonNode getSpecificPlantGuide(int id) throws JsonProcessingException {
//        if(!this.plant_guides.containsKey(id)){
//            /// Searching fasdasasghasjbg
//            for (int i : plant_guide_pages.keySet()) {
//                JsonNode json = plant_guide_pages.get(i);
//                JsonNode data = json.path("data");
//                for (JsonNode guide : data) {
//                    int id_ = guide.path("species_id").asInt(-1);
//                    if(id_ != -1) this.plant_guides.put(id_, guide);
//                    else continue;
//                    if(id_ == id) return guide;
//                }
//            }
//
//            /// Resolving undefined page
//            JsonNode next = plant_guide_pages.values().iterator().hasNext()? plant_guide_pages.values().iterator().next() : null;
//            if(next == null) next = getPlantGuide(1);
//            if(next != null) {
//                int last_page = next.path("last_page").asInt(-1);
//                for (int i = 1; i <= last_page; i++) { /// i = page
//                    if (plant_guide_pages.containsKey(i)) continue;
//                    JsonNode json = getPlantGuide(i);
//                    for (JsonNode guide : json.path("data")) {
//                        int id_ = guide.path("species_id").asInt(-1);
//                        if(id_ != -1) this.plant_guides.put(id_, guide);
//                        if(id_ == id) return guide;
//                    }
//                }
//            }
//
//            /// if not found throw this exception
//            throw new IllegalArgumentException("GUIDE TANAMAN TIDAK DITEMUKAN");
//        }
//        return this.plant_guides.get(id);
//    }
//
//    @SuppressWarnings({"Untuk sekarang jangan di gunakan jika memang tidak perlu",
//            "Cost = 1 request per unique id", "limit anggaran key request"})
//    public JsonNode getPlantHardiness(int id) throws JsonProcessingException {
//        if(this.plant_hardiness.containsKey(id)){
//            JsonNode json = rest.getForObject("https://perenual.com/api/hardiness-map?species_id="+id+"&key="+key, JsonNode.class);
//            update(Table.PLANT_HARDINESS, id, json);
//            return json;
//        }
//        return this.plant_hardiness.get(id);
//    }
}
