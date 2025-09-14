package com.trbear9.plants.api;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class Plant {
    public byte[] image
            ;
    public String description, family, nama_ilmiah, nama_umum, kategori, genus, kingdom, prune_url, difficulty;
    public int min_panen, max_panen;
    @Getter public final Map<String, String> kultur = new HashMap<>();
    @Getter public final Map<String, String> perawatan = new HashMap<>();
    @Getter public final Map<String, String> penyakit_dan_hama = new HashMap<>();
}
