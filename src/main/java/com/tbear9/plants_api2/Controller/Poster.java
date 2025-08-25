package com.tbear9.plants_api2.Controller;

import com.tbear9.plants_api2.DB;
import com.tbear9.plants_api2.Parameters;
import com.tbear9.plants_api2.UserVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/upload/")
public class Poster {
    @PostMapping()
    public String postSoil(@RequestBody UserVariable data){

    }
}
