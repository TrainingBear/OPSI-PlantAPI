package com.tbear9.plants_api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class API_Controller {
    @GetMapping("/who")
    public String hello_world(){
        return "jiter was here, https://github.com/TrainingBear";
    }

    @GetMapping
}
