package com.tbear9.plants_api2;

import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

class DBTest {

    private static final Logger log = LoggerFactory.getLogger("DATASET TEST");

    @Test
    public void getRecordWithParameters(){
        Parameters par1 = Parameters.UserParameters.builder()
                .category(E.CATEGORY.cereals_pseudocereals)
                .lifeSpan(E.LIFESPAM.annual)
                .panen(90)
                .build();
        Parameters par2 = Parameters.SoilParameters.builder()
                .O_drainage(E.DRAINAGE.poorly)
                .O_fertility(E.FERTILITY.high)
                .build();
        Parameters par3 = Parameters.GeoParameters.builder()
                .altitude(2000)
                .build();
        UserVariable userVariable = UserVariable.builder()
                .parameters(List.of(par1, par2, par3))
                .build();

        Map<Integer, CSVRecord> records = DB.getRecords(userVariable);
        Iterator<Integer> iterator = records.keySet().iterator();
        Integer score = iterator.next();
        CSVRecord best_score = records.get(score);
        log.info("best result with score: {}, cname: {} ", score, DB.getScienceName(best_score));
    }
}