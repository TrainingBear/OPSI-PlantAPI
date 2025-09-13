package com.trbear9.plants;

import com.trbear9.plants.api.*;
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
        Parameters par1 = CustomParameters.builder()
                .category(E.CATEGORY.cereals_pseudocereals)
                .lifeSpan(E.LIFESPAM.annual)
                .panen(90)
                .query("Oryza")
                .build();
        Parameters par2 = SoilParameters.builder()
                .O_drainage(E.DRAINAGE.poorly)
                .O_fertility(E.FERTILITY.high)
                .build();
        Parameters par3 = GeoParameters.builder()
                .altitude(2000)
                .iklim(iklim)
                .temperature(25)
                .rainfall(1700)
                .altitude(2300)
                .build();
        UserVariable userVariable = new UserVariable();
        userVariable.add(par1, par2, par3);

        DB.explored_fields = 0;
        Map<Integer, Set<CSVRecord>> records = DB.ecoCropDB_csv(userVariable);
        log.info("Explored fields: {}", DB.explored_fields);
        log.info("Best score with descending order");
        for (int score : records.keySet()) {
            for(CSVRecord bestValue : records.get(score))
                log.info("Best Value: {} with score of {}", DB.getScienceName(bestValue), score);
        }
    }
}