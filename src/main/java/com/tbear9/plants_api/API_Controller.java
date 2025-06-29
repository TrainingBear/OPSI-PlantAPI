package com.tbear9.plants_api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class API_Controller {
    @Autowired PerenualAPI perenual;

    @GetMapping("/who")
    public String hello_world(){
        return "jiter was here, https://github.com/TrainingBear";
    }

    @GetMapping("/api/plant")
    public String getPlantList(@RequestParam int page) throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(perenual.getPlantList(page));
    }

    @GetMapping("/api/plantdisease")
    public String getPlantDiseaseList(@RequestParam int page) throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(perenual.getPlantDiseaseList(page));
    }

    @GetMapping("/api/plantdisease")
    public String getSpecificPlantDisease(@RequestParam int id) throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(perenual.getSpecificPlantDisease(id));
    }
}
