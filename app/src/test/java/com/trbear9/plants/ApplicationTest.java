package com.trbear9.plants;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.List;

@SpringBootTest(classes = DataHandler.class)
public class ApplicationTest {
    Logger log = LoggerFactory.getLogger("ApplicationTest");

    @Test
    public void find2(){
//        CSVRecord record = DB.getRecord(Parameters.SoilParameters.builder()
//                        .depth(E.DEPTH.deep)
//                        .drainage(E.DRAINAGE.poorly)
//                .build());
//        log.info(record==null?"record1 is null!":"its real");
//        Assertions.assertNotNull(record);
//        log.info(record.get(E.Science_name));
    }
    public void find(){
        CSVRecord record = DataHandler.getRecord("Acacia", E.Authority);
        log.info(record==null?"record1 is null!":"its real");
        CSVRecord record2 = DataHandler.getRecord("Acacia", "ScientificName");
        log.info(record2==null?"record1 is null!":"its real");
    }
    @Test
    public void accesfile(){
    }

    @Test
    public void DB() {
        try (Reader in = new FileReader("EcoCrop_DB.csv")) {
            List<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in).getRecords();
            assert records != null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
