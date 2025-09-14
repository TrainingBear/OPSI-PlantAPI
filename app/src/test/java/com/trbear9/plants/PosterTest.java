package com.trbear9.plants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trbear9.plants.api.GeoParameters;
import com.trbear9.plants.api.Plant;
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

@SpringBootTest(classes = Poster.class)
public class PosterTest {
    private static final Logger log = LoggerFactory.getLogger(PosterTest.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static SimpleClientHttpRequestFactory requestFactory;
    private final static RestTemplate template;
    static {
        requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofDays(10));
        requestFactory.setConnectTimeout(Duration.ofDays(10));
        template = new RestTemplate(requestFactory);
    }

    @Test
    void test() throws JsonProcessingException {
        log.info("test initialized");
        String response = template.<String>getForObject(Poster.getUrl() + "/who", String.class);
        log.info("{}", response);
    }

    @Test
    void postVar() throws JsonProcessingException {
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
        UserVariable userVariable = new UserVariable();
        userVariable.add(geoParameters, soilParameters);
        userVariable.setImage(bos.toByteArray());
        userVariable.computeHash();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.<MediaType>singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<UserVariable> request = new HttpEntity<>(userVariable, headers);

        try {
            ResponseEntity<String> response = template.postForEntity(Poster.getUrl() +"/predict", request, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            log.info(root.toPrettyString());
        } catch (Exception e) {
            log.error("Cant procces your request");
            log.error("Error: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void predict() throws IOException {
//        File file = new File("fast_api/uploaded_images/aluvial-001.jpg");
//        float[] predict = FAService.predict(file);
//        log.info("predicts: {}", predict);
    }

    @Test
    public void makeDir(){
        File dir = new File("cache/responses");
        File file = new File(dir, System.nanoTime()+".json");
        if (dir.mkdirs()) {
            log.info("Directory created: {}", dir.getAbsolutePath());
        }
        else {
            log.info("Directory already exists: {}", dir.getAbsolutePath());
        }
        String json = "{\"state\": \"\test\"}";
//        if(!file.exists()) try {
//            objectMapper.writeValue(file, json);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    @Test void pykew(){
//        String urticaUrens = template.getForEntity(FAService.url+"/", String.class).getBody();
        String urticaUrens = template.<String>getForEntity(FAService.url+"/plants/"+"Urtica", String.class).getBody();
        log.info("{}", urticaUrens);
    }

    @Test
    public void getKewImage() throws JsonProcessingException {
        Poster poster = new Poster();
        String url = "https://powo.science.kew.org/api/1/search?q=Urtica";
        ResponseEntity<String> response = template.getForEntity(url, String.class);
        String body = response.getBody();
        JsonNode urtica = poster.getKew("Urtica");
        assert urtica != null;
        Plant plant = new Plant();
        plant.nama_ilmiah = "Urtica";
        byte[] urticas = poster.getImage(plant);
    }
    
}