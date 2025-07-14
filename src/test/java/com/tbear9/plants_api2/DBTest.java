package com.tbear9.plants_api2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PlantsApi2Application.class)
class DBTest {
    @Test
    public void getRecordWithParameters(){
        DB.Parameters par1 = DB.UserParameters.builder()
                .category(E.CATEGORY.cereals_pseudocereals)
                .lifeSpan(E.LIFESPAM.annual)
                .panen(1)
                .build();
    }
}