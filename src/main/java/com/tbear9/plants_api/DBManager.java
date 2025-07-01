package com.tbear9.plants_api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public final class DBManager {
    final static Logger log = LoggerFactory.getLogger("[Database Manager]");
    @Autowired JdbcTemplate template;
    public void init() throws JsonProcessingException {
        createTable();
        log.warn("Starting to deploy database to backend servers");
        for (API tabList : API.content_as_array) tabList.load();
    }

    public void createTable(){
        log.warn("Creating tables... ");
        for (Table value : Table.values()) {
            if(value.equals(Table.PLANT_HARDINESS)){
                String sql = "CREATE TABLE IF NOT EXISTS "+value.name+"(" +
                        "id int NOT NULL," +
                        " data MEDIUMBLOB," +
                        " CONSTRAINT UNIK UNIQUE (id)" +
                        ")";
                template.execute(sql);
                continue;
            }
            String sql = "CREATE TABLE IF NOT EXISTS "+value.name+"(" +
                    "id int NOT NULL," +
                    " data JSON," +
                    " CONSTRAINT UNIK UNIQUE (id)" +
                    ")";
            template.execute(sql);
        }
    }
}
