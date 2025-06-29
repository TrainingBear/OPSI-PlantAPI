package com.tbear9.plants_api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public final class DBManager {
    final static Logger log = LoggerFactory.getLogger(DBManager.class);
    final String key = PerenualAPI.key;
    @Autowired JdbcTemplate template;
    @Autowired PerenualAPI api;
    ObjectMapper mapper = new ObjectMapper();

    public void init(){
        createTable();
        try{
            deploy_database("plantlist");
            deploy_database("plantdiseaselist");
            deploy_database("plantdetails");
            deploy_database("plantguidelist");
            deploy_database("planthardness");

            deploy_collection(api.plant_list_pages, api.plants);
            deploy_collection(api.plant_disease_list_pages, api.plant_diseases);
            deploy_collection(api.plant_guide_pages, api.plant_guides);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void deploy_database(String database) throws JsonProcessingException {
        log.info("Deploying {}... ", database);
        String sql = "SELECT * FROM "+database;
        List<Map<String, Object>> maps = template.queryForList(sql);
        for (Map<String, Object> map : maps) {
            int id = (int) map.get("id");
            JsonNode json = mapper.readTree((String) map.get("data"));
            api.plant_list_pages.put(id, json);
        }
    }

    /**
     * Memecah data koleksi (yang berhalaman) menjadi data  yang lebih kecil
     * @param collection data yang akan di pecah
     * @param destination data tujuan
     */
    public void deploy_collection(Map<Integer, JsonNode> collection,
                                  Map<Integer, JsonNode> destination){
        for (JsonNode value : collection.values()) {
            for (JsonNode data : value.path("data")) {
                int id = data.path("id").asInt();
                destination.put(id, data);
            }
        }
    }

    public void createTable(){
        log.info("Creating tables... ");
        String plantlist = "CREATE TABLE IF NOT EXISTS plantlist(" +
                "id int NOT NULL," +
                " data JSON," +
                " CONSTRAINT UNIK UNIQUE (id)" +
                ")";
        String plantdisseaselist = "CREATE TABLE IF NOT EXISTS plantdiseaselist(" +
                "id int NOT NULL UNIQUE," +
                " data JSON," +
                " CONSTRAINT UNIK UNIQUE (id)" +
                ")";
        String plantguidelist = "CREATE TABLE IF NOT EXISTS plantguidelist(" +
                "id int NOT NULL UNIQUE," +
                " data JSON," +
                " CONSTRAINT UNIK UNIQUE (id)" +
                ")";
        String plantdetails = "CREATE TABLE IF NOT EXISTS plantdetails(" +
                "id int NOT NULL UNIQUE," +
                " data JSON," +
                " CONSTRAINT UNIK UNIQUE (id)" +
                ")";
        String planthardness = "CREATE TABLE IF NOT EXISTS planthardness(" +
                "id int NOT NULL UNIQUE," +
                " data JSON," +
                " CONSTRAINT UNIK UNIQUE (id)" +
                ")";

        template.execute(plantlist);
        template.execute(plantdisseaselist);
        template.execute(plantguidelist);
        template.execute(plantdetails);
        template.execute(planthardness);
    }
}
