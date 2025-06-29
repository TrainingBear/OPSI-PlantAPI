package com.tbear9.plants_api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class API_Controller {
    @Autowired PerenualAPI perenual;

    @GetMapping("/who")
    public String hello_world(){
        return "jiter was here, https://github.com/TrainingBear";
    }

    @GetMapping("/api/plant/list")
    public String getPlantsList(@RequestParam int page) throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(perenual.getPlantList(page));
    }

    @GetMapping("/api/plant")
    public String getSpecificPlant(@RequestParam int id) throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(perenual.getSpecificPlant(id));
    }

    @GetMapping("/api/plant-details")
    public String getSpecificPlantDetails(@RequestParam int id) throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(perenual.getSpecificPlantDetails(id));
    }

    @GetMapping("/api/plant-disease/list")
    public String getPlantDiseaseList(@RequestParam int page) throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(perenual.getPlantDiseaseList(page));
    }

    @GetMapping("/api/plant-disease")
    public String getSpecificPlantDisease(@RequestParam int id) throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(perenual.getSpecificPlantDisease(id));
    }

    @GetMapping("/api/plant-guide/list")
    public String getPlantGuideList(@RequestParam int page) throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(perenual.getPlantGuide(page));
    }

    @GetMapping("/api/plant-guide")
    public String getSpecificPlantGuide(@RequestParam int id) throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(perenual.getSpecificPlantGuide(id));
    }

    @GetMapping("/api/plant-hardiness")
    public String getPlantHardiness(@RequestParam int id) throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(perenual.getPlantHardiness(id));
    }
}
