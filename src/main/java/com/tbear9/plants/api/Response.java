package com.tbear9.plants.api;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Builder
public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 3182025L;
    private String soilName;
    private Map<Integer, List<Map<String, String>>> tanaman; //{score: [{nama tanaman: response rag}, {...}]}

    public void put(int score, String nama_ilmiah, String response){
        put(score, Map.of(nama_ilmiah, response));
    }
    public void put(int score, Map<String, String> response){
        tanaman.computeIfAbsent(score, k -> new ArrayList<>()).add(response);
    }
}
