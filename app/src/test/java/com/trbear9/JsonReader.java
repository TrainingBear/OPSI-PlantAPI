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
    void read() throws IOException {
        File file = new File("open_ai/cache/11327106226062.json");
        JsonNode jsonNode = objectMapper.readTree(file);
        String text = jsonNode.asText();
        System.out.println(text);
        jsonNode = objectMapper.readTree(text);
        File responses = new File("open_ai/responses");
        log.info("Reading responses... ");
        for (File response : Objects.requireNonNull(responses.listFiles())) {
            JsonNode node = objectMapper.readTree(response);
            String text1 = node.asText();
            System.out.println(text1);
            node = objectMapper.readTree(text1);
            log.info("\n With outputs: \n ");
            JsonNode jsonNode1 = node.get("output");
            assert jsonNode1 != null;
            System.out.println(
                    jsonNode1
                            .get(1)
                            .get("content")
                            .get(0)
                            .get("text"));
            log.info("\n \n \n \n \n \n");
        }
    }
}
