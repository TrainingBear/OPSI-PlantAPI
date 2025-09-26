package com.trbear9.plants.api.blob;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlantCare implements Serializable {
    private String watering;
    private String pruning;
    private String fertilization;
    private String sunlight;

    @JsonProperty("pest_disease_management")
    private String pestDiseaseManagement;
}

