package com.trbear9.plants.api;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Plant {
    public byte[] thumbnail;
    public byte[] fullsize;
    public String description, family, nama_ilmiah,
            nama_umum, kategori, genus, kingdom,
            prune_url, difficulty, taxon;
    public int min_panen, max_panen;
    @Getter public final Map<String, String> kultur = new HashMap<>();
    @Getter public final Map<String, String> perawatan = new HashMap<>();
}
