package com.tbear9.plants_api2;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

class FAServiceTest {

    private static final Logger log = LoggerFactory.getLogger(FAServiceTest.class);

    @Test
    void process() throws IOException {
        File file = new File("fast_api/uploaded_images/humus-110.jpg");
        float[] predict = FAService.predict(file);
        log.info("predict: {}", predict);
        log.info("argmax: {}", FAService.argmax(predict));
    }
}