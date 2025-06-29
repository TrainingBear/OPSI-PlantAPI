package com.tbear9.plants_api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

public final class Database {
    final Logger log = LoggerFactory.getLogger(this.getClass());
    final String key = PerenualAPI.key;
    static JdbcTemplate template =  PlantsApiApplication.getTemplate();
    static RestTemplate rest = new RestTemplate();

    public static void load(){
        String sql = "SELECT * FROM plantlist";
        template.query(sql,  result -> {
            int page = result.getInt(1);
        });
    }



}
