package com.tbear9.plants_api;

import com.tbear9.plants_api2.DB;
import com.tbear9.plants_api2.E;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.URL;
import java.util.List;

@SpringBootTest(classes = DB.class)
public class ApplicationTest {
    Logger log = LoggerFactory.getLogger("ApplicationTest");

    @Test
    public void find2(){
        CSVRecord record = DB.getRecord(DB.SoilParameters.builder()
                        .O_depth(E.DEPTH.deep)
                        .O_drainage(E.DRAINAGE.poorly)
                .build());
        log.info(record==null?"record1 is null!":"its real");
        Assertions.assertNotNull(record);
        log.info(record.get(E.Science_name));
    }
    public void find(){
        CSVRecord record = DB.getRecord("Acacia", E.Authority);
        log.info(record==null?"record1 is null!":"its real");
        CSVRecord record2 = DB.getRecord("Acacia", "ScientificName");
        log.info(record2==null?"record1 is null!":"its real");
    }
    @Test
    public void accesfile(){
    }

    @Test
    public void DB() {
        try (Reader in = new FileReader("EcoCrop_DB.csv")) {
            List<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in).getRecords();
            records.forEach(record -> {
                log.info(record.get("AUTH"));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
