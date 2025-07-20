package com.tbear9.plants_api2;

import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class DBTest {
    private static final Logger log = LoggerFactory.getLogger("DATASET TEST");

    @Test
    public void getRecordWithParameters(){
        E.CLIMATE iklim = E.CLIMATE.tropical_wet;
        Parameters par1 = Parameters.UserParameters.builder()
                .category(E.CATEGORY.cereals_pseudocereals)
                .lifeSpan(E.LIFESPAM.annual)
                .panen(90)
                .query("Oryza")
                .build();
        Parameters par2 = Parameters.SoilParameters.builder()
                .O_drainage(E.DRAINAGE.poorly)
                .O_fertility(E.FERTILITY.high)
                .build();
        Parameters par3 = Parameters.GeoParameters.builder()
                .altitude(2000)
                .iklim(iklim)
                .temperature(25)
                .rainfall(1700)
                .altitude(2300)
                .build();
        UserVariable userVariable = UserVariable.builder()
                .parameters(List.of(par1, par2, par3))
                .build();

        DB.explored_fields = 0;
        Map<CSVRecord, Integer> records = DB.getRecords(userVariable);
        int score = Integer.MIN_VALUE;
        Set<CSVRecord> best_values = new HashSet<>();
        for(CSVRecord i : records.keySet()){
            int skor = records.get(i);
            if(skor >= score){
                score = skor;
                best_values.add(i);
            }
            log.info("Score= {}, Science Name= {} ", skor, DB.getScienceName(i));
        }
        log.info("Explored fields: {}", DB.explored_fields);
        for (CSVRecord bestValue : best_values) {
            log.info("Best Value: {}", DB.getScienceName(bestValue));
        }
    }
}