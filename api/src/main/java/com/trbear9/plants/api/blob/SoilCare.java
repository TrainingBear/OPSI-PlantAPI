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
public class SoilCare {
    @JsonProperty("pH_correction")
    private String pHCorrection;

    @JsonProperty("organic_matter")
    private String organicMatter;

    @JsonProperty("water_retention")
    private String waterRetention;

    @JsonProperty("nutrient_management")
    private NutrientManagement nutrientManagement;

}
