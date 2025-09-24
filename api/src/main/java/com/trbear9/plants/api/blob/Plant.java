package com.trbear9.plants.api.blob;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Plant {
    byte[] fullsize;
    byte[] thumbnail;
    @JsonProperty("nama_ilmiah")
    private String nama_ilmiah;
    private String family;
    private String genus;
    private String kingdom;
    private String taxon;
    private String kategori;
    @JsonProperty("min_panen")
    private int min_panen;
    @JsonProperty("max_panen")
    private int max_panen;
    @JsonProperty("plant_care")
    private PlantCare plantCare;

    private String difficulty;
    private String description;

    @JsonProperty("product_system")
    private ProductSystem productSystem;

    @JsonProperty("common_name")
    private String commonName;

    @JsonProperty("prune_guide")
    private String pruneGuide;
}

