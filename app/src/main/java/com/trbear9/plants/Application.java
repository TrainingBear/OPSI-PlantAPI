package com.trbear9.plants;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.process.NgrokProcess;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
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
        NgrokClient ngrokClient = new NgrokClient.Builder().build();

        Tunnel tunnel = ngrokClient.connect();
        CreateTunnel createTunnel = new CreateTunnel.Builder()
                .withAddr(8080)
                .withProto(Proto.HTTP)
                .build();
    }

    @Override
    public void run(String... args) throws Exception {
        FAService.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down... ");
            FAService.stop();
        }));
    }


    public static void process() throws IOException, IOException {
        File file = new File("fast_api/uploaded_images/humus-110.jpg");
        float[] predict = FAService.predict(file);
        log.info("predicts: {}", predict);
        log.info("argmax: {}", FAService.argmax(predict));
    }
}
