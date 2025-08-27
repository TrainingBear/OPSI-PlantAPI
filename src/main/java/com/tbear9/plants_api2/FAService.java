package com.tbear9.plants_api2;

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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.*;

public final class FAService {
    public static final RestTemplate template = new RestTemplate();
    public static final Logger log = LoggerFactory.getLogger("FAService");
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

    public static final Parameters.SoilParameters[] soil = {
            Parameters.SoilParameters.ALLUVIAL,
            Parameters.SoilParameters.ANDOSOL,
            Parameters.SoilParameters.ENTISOL,
            Parameters.SoilParameters.HUMUS,
            Parameters.SoilParameters.INCEPTISOL,
            Parameters.SoilParameters.LATERITE,
            Parameters.SoilParameters.KAPUR,
            Parameters.SoilParameters.PASIR
    };


    public static Parameters.SoilParameters process(byte[] image){
        float[] prediction = predict(image);
        return soil[argmax(prediction)];
    }

    public static float[] predict(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", bos);
        return predict(bos.toByteArray());
    }
    public static float[] predict(byte[] img){
        ByteArrayResource imgResource = new ByteArrayResource(img){
            @Override
            public String getFilename() {
                return "request.jpg";
            }
        };

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("file", imgResource);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "multipart/form-data");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = template.postForEntity("http://127.0.0.1:8000/predict", request, String.class);
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
