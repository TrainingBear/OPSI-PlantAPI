package com.trbear9;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class JsonReader {
    private static final Logger log = LoggerFactory.getLogger(JsonReader.class);
    ObjectMapper objectMapper = new ObjectMapper();
    @Test
    void readRagResponses() throws IOException {
        File responses = new File("cache/responses");
        log.info("Reading responses... ");
        for (File response : Objects.requireNonNull(responses.listFiles())) {
            JsonNode node = objectMapper.readTree(response);
            String text1 = node.asText();
            System.out.println(text1);
            node = objectMapper.readTree(text1);
            log.info("\n With outputs: \n ");
            JsonNode jsonNode1 = node.get("output");
            assert jsonNode1 != null;
            JsonNode text = jsonNode1
                    .get(1)
                    .get("content")
                    .get(0)
                    .get("text");
            System.out.println(text.asText());
            log.info("\n \n \n \n \n \n");
        }
    }

    @Test
    void readKew_caches(){
        File kew_caches = new File("cache/kew_caches");
        for (File file : kew_caches.listFiles()) {
            if(file.getName().endsWith(".json")){
                try {
                    JsonNode node = objectMapper.readTree(file);
                    node = objectMapper.readTree(node.asText());
                    log.info("{} values: ", file.getName());
                    log.info(node.toPrettyString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
