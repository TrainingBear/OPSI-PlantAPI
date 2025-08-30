package com.tbear9.plants;

import com.tbear9.plants.api.SoilParameters;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public final class FAService {
    public static final RestTemplate template = new RestTemplate();
    public static final Logger log = LoggerFactory.getLogger("FAService");
    public static final String key = "?";
    public static String url = null;
    private static Process process = null;
    public final static String[] label = {
            "Aluvial",
            "Andosol",
            "Entisol",
            "Humus",
            "Inceptisol",
            "Laterit",
            "Kapur",
            "Pasir"
    };

    public static final SoilParameters[] soil = {
            SoilParameters.ALLUVIAL,
            SoilParameters.ANDOSOL,
            SoilParameters.ENTISOL,
            SoilParameters.HUMUS,
            SoilParameters.INCEPTISOL,
            SoilParameters.LATERITE,
            SoilParameters.KAPUR,
            SoilParameters.PASIR
    };
    public static void start() throws IOException, InterruptedException {
        log.info("Starting fastapi service... ");
        ProcessBuilder pb = new ProcessBuilder("fastapi", "run", "fast_api/api.py");
//        ProcessBuilder pb = new ProcessBuilder("gnome-terminal", "--", "bash", "-c", "fastapi run fast_api/api.py");
        long start = System.nanoTime();
        process = pb.start();
        if(!process.isAlive()){
            log.error("API failed to start");
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while((line= reader.readLine())!= null) {
            if(line.contains("Server started")){
                String[] s = line.split(" ");
                url = s[10];
                String took = String.format("%.2fms", (System.nanoTime() - start) / 1_000_000.0);
                log.info("API {} started in {}", url, took);
                Application.process();
            }
            log.info(line);
        }
    }

    public static void stop(){
        if(process != null)
            process.destroy();
    }
    public static SoilParameters process(byte[] image){
        float[] prediction = predict(image);
        return soil[argmax(prediction)];
    }

    public static float[] predict(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", bos);
        return predict(bos.toByteArray());
    }
    public static float @NonNull [] predict(byte[] img){
        ByteArrayResource imgResource = new ByteArrayResource(img){
            @Override
            public String getFilename() {
                return "request.jpg";
            }
        };

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("file", imgResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response;
        try {
             response = template.postForEntity(url+"/predict", request, String.class);
        } catch (IllegalArgumentException e){
            log.error("need the actual api, but url expected {}", url);
            throw e;
        }
        String sjson = response.getBody();

        if(sjson==null) return null;
        float[] logits = new float[soil.length];
        sjson = sjson.replaceAll("[\\[\\]]", "");
        String[] farray = sjson.split(",");
        for (int i = 0; i < soil.length; i++)
            logits[i] = Float.parseFloat(farray[i]);
        return logits;
    }

    public static int argmax(float[] prediction){
        float max = Float.MIN_VALUE;
        int ans = -1;
        for (int i = 0; i < prediction.length; i++) {
            if(prediction[i] > max){
                max = prediction[i];
                ans = i;
            }
        }
        return ans;
    }
    /**
     * @param logits output dari prediksi linear. yang akan di normalisasi dengan softmax function
     */
    public static void soft(float[] logits){
        {
            /* SOFT MAX
            * SoftMax(i) = (e[Z[i]]) / sum(exp(logits))) */
            float max = Float.MIN_VALUE;
            for(float i : logits)
                max = Math.max(i, max);

            double sumExp = 0d;
            for(int i = 0; i < logits.length; i++){
                logits[i] = (float) Math.exp(logits[i] - max);
                sumExp += logits[i];
            }
            for(int i = 0; i < logits.length; i++)
                logits[i] = (float) (logits[i] / sumExp);
        }
    }
}
