package com.trbear9.plants.api;

import lombok.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    public String hashCode;
    private String soilName;
    private float[] soilPrediction = new float[8];
    private int total = 0;
    private double predict_time, process_time, took;
    private Map<Integer, List<Plant>> tanaman = new HashMap<>();
    //{score: [{nama tanaman: response rag}, {...}]}

    public void put(int score, Plant plant){
        tanaman.computeIfAbsent(score, k -> new ArrayList<>()).add(plant);
        total++;
    }
}
