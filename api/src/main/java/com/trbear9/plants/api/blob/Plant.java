package com.trbear9.plants.api.blob;

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

