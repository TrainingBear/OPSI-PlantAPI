package com.trbear9.plants;

import lombok.val;
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
        boolean[] loaded = new boolean[4];
        for (String arg : args) {
            if(arg.startsWith("--GIST-PROVIDER=")) {
                val substring = arg.substring("--GIST-PROVIDER=".length());
                val provider = substring.split("/");
                if (provider.length != 2) {
                    throw new RuntimeException("Please provide valid GIST provider, read documentation at: https://github.com/TrainingBear/OPSI-PlantAPI");
                }
                System.setProperty("GITHUB_USER", provider[0]);
                System.setProperty("GIST_ID", provider[1]);
                log.info("Loaded GIST provider: {}", substring);
                loaded[0] = true;
            }
            else if(arg.startsWith("--GITHUB-TOKEN=")){
                System.setProperty("GITHUB_TOKEN", arg.substring("--GITHUB-TOKEN".length()+1));
                log.info("Loaded GITHUB token: {}", System.getProperty("GITHUB_TOKEN"));
                loaded[1] = true;
            }
            else if(arg.startsWith("--OPEN-AI-KEY=")){
                System.setProperty("OPEN_AI_KEY", arg.substring("--OPEN-AI-KEY".length()+1));
                log.info("Loaded OPEN_AI_KEY: {}", System.getProperty("OPEN_AI_KEY"));
                loaded[2] = true;
            }
            else if(arg.startsWith("--MODEL=")){
                System.setProperty("MODEL", arg.substring("--MODEL".length()+1));
                log.info("Loaded MODEL: {}", System.getProperty("MODEL"));
                loaded[3] = true;
            }
            else {
                log.error("Unknown argument: {}", arg);
                throw new RuntimeException("Please provide all required arguments, read documentation at: https://github.com/TrainingBear/OPSI-PlantAPI");
            }
        }
        for(boolean isLoaded : loaded)
            if(!isLoaded) throw new RuntimeException("Please provide all required arguments, read documentation at: https://github.com/TrainingBear/OPSI-PlantAPI");

        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {

        FastApiService.start();
        ServerHandler.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down... ");
            FastApiService.stop();
            ServerHandler.stop();
        }));
    }


    public static void process() throws IOException, IOException {
        File file = new File("fast_api/uploaded_images/humus-110.jpg");
        float[] predict = FastApiService.predict(file);
        log.info("predicts: {}", predict);
        log.info("argmax: {}", FastApiService.argmax(predict));
    }
}
