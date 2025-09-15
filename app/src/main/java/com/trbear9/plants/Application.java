package com.trbear9.plants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class Application implements CommandLineRunner {
    public static final Logger log = LoggerFactory.getLogger("Application");
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        FAService.start();
        ServerHandler.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down... ");
            FAService.stop();
            ServerHandler.stop();
        }));
    }


    public static void process() throws IOException, IOException {
        File file = new File("fast_api/uploaded_images/humus-110.jpg");
        float[] predict = FAService.predict(file);
        log.info("predicts: {}", predict);
        log.info("argmax: {}", FAService.argmax(predict));
    }
}
