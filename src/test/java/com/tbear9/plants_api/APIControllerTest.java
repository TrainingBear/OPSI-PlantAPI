package com.tbear9.plants_api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class APIControllerTest {
    @Autowired API_Controller controller;

    @Test
    void getPlantsList() {
        try {
            String json = controller.getPlantsList(1);
            assert(json != null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getSpecificPlant() {
        try {
            String json = controller.getSpecificPlant(1);
            assert(json != null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getSpecificPlantDetails() {
        try {
            String json = controller.getSpecificPlantDetails(1);
            assert(json != null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getPlantDiseaseList() {
        try {
            String json = controller.getPlantDiseaseList(1);
            assert(json != null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getSpecificPlantDisease() {
        try {
            String json = controller.getSpecificPlantDisease(2);
            assert(json != null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getPlantGuideList() {
        try {
            String json = controller.getPlantGuideList(1);
            assert(json != null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getSpecificPlantGuide() {
        try {
            String json = controller.getSpecificPlantGuide(1);
            assert(json != null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getPlantHardiness() {
        try {
            String json = controller.getPlantHardiness(1);
            assert(json != null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}