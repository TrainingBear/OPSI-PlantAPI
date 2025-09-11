package com.trbear9.plants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trbear9.plants.api.GeoParameters;
import com.trbear9.plants.api.SoilParameters;
import com.trbear9.plants.api.UserVariable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static com.trbear9.plants.E.CLIMATE.*;
import static com.trbear9.plants.E.DEPTH.*;

public class PosterTest {
    private static final Logger log = LoggerFactory.getLogger(PosterTest.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static SimpleClientHttpRequestFactory requestFactory;
    private final static RestTemplate template;
    static {
        requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofMinutes(2));
        requestFactory.setConnectTimeout(Duration.ofMinutes(2));
        template = new RestTemplate(requestFactory);
    }

    @Test
    void test(){
        log.info("test initialized");
        String response = template.getForObject("http://localhost:8080" + "/who", String.class);
        log.info("{}", response);
    }

    @Test
    void postVar(){
        File file = new File("fast_api/uploaded_images/aluvial-001.jpg");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            BufferedImage img = ImageIO.read(file);
            ImageIO.write(img, "jpg", bos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        GeoParameters geoParameters = GeoParameters.builder()
                .iklim(temperate_with_humid_winters)
                .build();
        SoilParameters soilParameters = SoilParameters.builder()
                .O_depth(deep)
                .build();
        UserVariable userVariable = UserVariable.builder()
                .parameter(geoParameters)
                .parameter(soilParameters).image(bos.toByteArray())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<UserVariable> request = new HttpEntity<>(userVariable, headers);
        try {
            ResponseEntity<String> response = template.postForEntity("http://localhost:8080"+"/predict", request, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            File output = new File("restapi_outputs", file.getName() + ".json");
            output.mkdirs();
            if(!output.exists()) {
                objectMapper.writeValue(output, root);
                log.info("Object has been written at {}", output.getAbsolutePath());
                return;
            }
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        };
        log.error("Cant get a response from your request!");
    }
}