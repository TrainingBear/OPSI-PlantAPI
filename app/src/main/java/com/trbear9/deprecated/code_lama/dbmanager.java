package com.trbear9.deprecated.code_lama;

public class dbmanager {
//    public void init(){
//        createTable();
//        try{
//            log.warn("Starting to deploy database to backend servers");
//            for(Table table: Table.values()) deploy_database(table);
//
//            deploy_collection(api.plant_list_pages, api.plants);
//            deploy_collection(api.plant_disease_list_pages, api.plant_diseases);
//            deploy_collection(api.plant_guide_pages, api.plant_guides);
//
//            log.info("Deploying sudah selesai dengan hasil : ");
//            log.info("  Species tanaman yang terdaftar : {}", api.plants.size());
//            log.info("  Hama tanaman yang terdaftar : {}", api.plant_diseases.size());
//            log.info("  Panduan tanaman yang terdaftar : {}", api.plant_guides.size());
//            log.info("  Lokasi/Suhu/Iklim/hardiness tanaman yang terdaftar : {}", api.plant_hardiness.size());
//            log.info("  Detail tanaman yang terdaftar : {}", api.plant_details.size());
//            log.info("  Total halaman species yang terindex : {}", api.plant_list_pages.size());
//            log.info("  Total halaman disease yang terindex : {}", api.plant_disease_list_pages.size());
//            log.info("  Total halaman guide yang terindex : {}", api.plant_guide_pages.size());
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void deploy_database(Table table) throws JsonProcessingException {
//        log.info("Deploying {}... ", table.name);
//        String sql = "SELECT * FROM "+table.name;
//        long startTime = System.nanoTime();
//        List<Map<String, Object>> maps = template.queryForList(sql);
//        StringBuilder  builder = new StringBuilder();
//        for (Map<String, Object> map : maps) {
//            int id = (int) map.get("id");
//            JsonNode json = mapper.readTree((String) map.get("data"));
//            builder.append(id).append(", ");
//            switch (table){
//                case PLANT_LIST -> api.plant_list_pages.put(id, json);
//                case PLANT_DISEASE_LIST -> api.plant_disease_list_pages.put(id, json);
//                case PLANT_GUIDE_LIST -> api.plant_guide_pages.put(id, json);
//                case PLANT_DETAILS -> api.plant_details.put(id, json);
//                case PLANT_GUIDE_DETAILS -> api.plant_guides.put(id, json);
//                case PLANT_HARDINESS -> api.plant_hardiness.put(id, json);
//            }
//        }
//        long endTime = System.nanoTime();
//        String time = String.format("%.2f", (endTime - startTime) / 1_000_000.0);
//        log.info("DONE! Tabel {} menghabiskan waktu {} ms!", table.name, time);
//    }
//
//    /**
//     * Memecah data koleksi (yang berhalaman) menjadi data  yang lebih kecil
//     * @param collection data yang akan di pecah
//     * @param destination data tujuan
//     */
//    public void deploy_collection(Map<Integer, JsonNode> collection,
//                                  Map<Integer, JsonNode> destination){
//        for (JsonNode value : collection.values()) {
//            for (JsonNode data : value.path("data")) {
//                int id = data.path("id").asInt();
//                destination.put(id, data);
//            }
//        }
//    }
//
}
