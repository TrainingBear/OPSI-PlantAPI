package com.trbear9.plants;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

class FAServiceTest {

    private static final Logger log = LoggerFactory.getLogger(FAServiceTest.class);

    @Test
    public void process() throws IOException {
        File file = new File("fast_api/uploaded_images/humus-110.jpg");
        float[] predict = FAService.predict(file);
        log.info("predict: {}", predict);
        log.info("argmax: {}", FAService.argmax(predict));
    }

    @Test
    void runCommand() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("echo", "$JAVA_HOME");
        Process process = pb.start();
        process.info().user();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            log.info(line);
        }

        int exit = process.waitFor();
        log.info("exit: {}", exit);
    }

    @Test
    void runPython() throws IOException, InterruptedException {
//        FAService.start();
    }

    @Test
    void createTerminal() throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.start();
    }
}