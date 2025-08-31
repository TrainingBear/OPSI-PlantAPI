package com.trbear9.plants_api;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class APIControllerTest {
//    @Autowired
//    APIController controller;
//
//    @Test
//    void getPlantsList() {
//        try {
//            String json = controller.getPlantsList(1);
//            assert(json != null);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    void getSpecificPlant() {
//        try {
//            String json = controller.getSpecificPlant(1);
//            assert(json != null);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    void getSpecificPlantDetails() {
//        try {
//            String json = controller.getSpecificPlantDetails(1);
//            assert(json != null);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    void getPlantDiseaseList() {
//        try {
//            String json = controller.getPlantDiseaseList(1);
//            assert(json != null);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    void getSpecificPlantDisease() {
//        try {
//            String json = controller.getSpecificPlantDisease(2);
//            assert(json != null);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    void getPlantGuideList() {
//        try {
//            String json = controller.getPlantGuideList(1);
//            assert(json != null);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    void getSpecificPlantGuide() {
//        try {
//            String json = controller.getSpecificPlantGuide(1);
//            assert(json != null);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    void getPlantHardiness() {
//        try {
//            byte[] json = controller.getPlantHardiness(1);
//            assert(json != null);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    void GET_ALL(){
//        RestTemplate template = new RestTemplate();
//        JsonNode test1 = template.getForObject("http://localhost:8080/api/plant/list?page=1", JsonNode.class);
//        assert(test1!=null);
//        JsonNode test2 = template.getForObject("http://localhost:8080/api/plant-disease/list?page=1", JsonNode.class);
//        assert(test2!=null);
//        JsonNode test3 = template.getForObject("http://localhost:8080/api/plant-guide/list?page=1", JsonNode.class);
//        assert(test3!=null);
//        byte[] test4 = template.getForObject("http://localhost:8080/api/plant-hardiness?id=1", byte[].class);
//        assert(test4!=null);
//        JsonNode test5 = template.getForObject("http://localhost:8080/api/plant?id=1", JsonNode.class);
//        assert(test5!=null);
//        JsonNode test6 = template.getForObject("http://localhost:8080/api/plant-details?id=1", JsonNode.class);
//        assert(test6!=null);
//        JsonNode test7 = template.getForObject("http://localhost:8080/api/plant-disease?id=1", JsonNode.class);
//        assert(test7!=null);
//        JsonNode test8 = template.getForObject("http://localhost:8080/api/plant-guide?id=1", JsonNode.class);
//        assert(test8!=null);
//        String test9 = template.getForObject("http://localhost:8080/who", String.class);
//        assert(test9!=null);
//    }
//
}