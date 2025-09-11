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
    private String soilName;
    private Map<Integer, List<Pair<String, String>>> tanaman = new HashMap<>();
    //{score: [{nama tanaman: response rag}, {...}]}

    public void put(int score, String nama_ilmiah, String response){
        put(score, Pair.of(nama_ilmiah, response));
    }
    public void put(int score, Pair<String, String> response){
        tanaman.computeIfAbsent(score, k -> new ArrayList<>()).add(response);
    }
}
