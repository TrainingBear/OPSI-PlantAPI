package com.trbear9.plants.api;

import com.trbear9.plants.api.blob.Plant;
import com.trbear9.plants.api.blob.SoilCare;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response implements Serializable {
    public String timestamp, status, error, trace, message, path, hashCode;
    private String soilName;
    private SoilCare soilCare;
    private float[] soilPrediction = new float[8];
    private int total = 0;
    private double altitude = 538;
    private double predict_time, process_time, took;
    private Map<Integer, List<Plant>> tanaman = new HashMap<>();
    //{score: [{nama tanaman: response rag}, {...}]}

    public void put(int score, Plant plant){
        tanaman.computeIfAbsent(score, k -> new ArrayList<>()).add(plant);
        total++;
    }
}
