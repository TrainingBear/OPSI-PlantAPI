package com.trbear9.plants;

import com.trbear9.plants.api.GeoParameters;
import com.trbear9.plants.E.CLIMATE;
import com.trbear9.plants.api.SoilParameters;
import com.trbear9.plants.api.UserVariable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.trbear9.plants.E.CLIMATE.*;
import static com.trbear9.plants.E.DEPTH.*;

@SpringBootTest
class PosterTest {
    private static final Logger log = LoggerFactory.getLogger(PosterTest.class);

    @Test
    void test(){
        log.info("test initialized");
    }

    @Test
    void postVar(){
        File file = new File("fast_api/uploaded_mages/aluvial-001.jpg");
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
                .parameter(soilParameters)
                .image(bos.toByteArray())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HashMap<String, Object> map = new HashMap<>();
    }
}