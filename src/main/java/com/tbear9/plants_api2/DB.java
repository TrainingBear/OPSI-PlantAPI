package com.tbear9.plants_api2;

import lombok.Builder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class DB {
    public static final Logger log = LoggerFactory.getLogger("DATASET LOG");
    public static String getScienceName(CSVRecord record){return record.get(E.Science_name);}
    public static String[] getCommonName(CSVRecord record){return record.get(E.Common_names).split(",");}

    public static Map<Integer, CSVRecord> getRecords(UserVariable userVariable){
        Map<Integer, CSVRecord> map = new HashMap<>();
        Set<? extends Parameters> parameters = userVariable.getParameters();

        for (CSVRecord record : getRecords()) {
            int score = 0;
            boolean flag = false;
            for (Parameters parameter : parameters) {
                Map<String, String> par_ = parameter.getParameters();
                for (String col : par_.keySet()) {
                    final String var = par_.get(col);
                    switch (col){
                        case "LAT" -> {
                            float min = Float.parseFloat(record.get(E.O_minimum_latitude).equals( "NA") ? "0" : record.get(E.O_minimum_latitude));
                            float max = Float.parseFloat(record.get(E.O_maximum_latitude).equals("NA") ? "0" : record.get(E.A_maximum_latitude));
                            float i = Float.parseFloat(var);
                            if(i <= max && i >= min){
                                score += 1;
                                flag = true;
                            }
                        }
                        case "ALT" -> {
                            float altitude = Float.parseFloat(record.get(E.A_minimum_altitude).equals("NA")? "0" : record.get(E.A_minimum_altitude));
                            if(altitude <= Float.parseFloat(var)){
                                score += 1;
                                flag = true;
                            }
                        }
                        case "RAIN" -> {
                            float min = Float.parseFloat(record.get(E.O_minimum_rainfall));
                            float max = Float.parseFloat(record.get(E.O_maximum_rainfall));
                            if (min <= max && min <= Float.parseFloat(var)) {
                                score += 1;
                                flag = true;
                            }
                        }
                        case "TEMP" -> {
                            float min = Float.parseFloat(record.get(E.O_minimum_temperature));
                            float max = Float.parseFloat(record.get(E.O_maximum_temperature));
                            if (min <= max && min <= Float.parseFloat(var)) {
                                score += 1;
                                flag = true;
                            }
                        }

                        case "PANEN" -> {
                            float min = Float.parseFloat(record.get(E.MIN_crop_cycle));
                            float max = Float.parseFloat(record.get(E.MAX_crop_cycle));
                            if (min <= max && min <= Float.parseFloat(var)) {
                                score += 1;
                                flag = true;
                            }
                        }

                        case "QUERY" -> {
                            if (record.get(E.Common_names).contains(var)) {
                                score += 1;
                                flag = true;
                            }
                        }

                        default -> {
                            String row = record.get(col);
                            if(row.contains(var)){
                                score += 1;
                                flag = true;
                            }
                        }
                    }
                }
            }
            if(flag) map.put(score, record);
        }
        return new TreeMap<>(map);
    }

    public static CSVRecord getRecord(Parameters parameters){
        Map<String, String> par = parameters.getParameters();
        log.info("finding... ");
        for (CSVRecord record : getRecords()) {
            boolean flag = false;
            for (String col : par.keySet()) {
                String val = par.get(col);
                if(val==null) continue;
                log.info("checking {}", record.get(col));
                if(record.get(col).contains(val)) {
                    flag = true;
                }
            }
            if(flag) return record;
        }
        return null;
    }
    public static CSVRecord getRecord(String query, String column){
        log.info("finding {} in {}", query, column);
        for (CSVRecord i : getRecords()) {
//            log.info("checking {}", i.get(column));
            if (i.get(column).contains(query)) {
                return i;
            }
        }
        log.info("{} not found in {}", query, column);
        return null;
    }

    public static List<CSVRecord> getRecords(){
        try (Reader in = new FileReader("EcoCrop_DB.csv")) {
            return CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in).getRecords();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
