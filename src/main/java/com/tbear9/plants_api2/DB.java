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
    public static int explored_fields = 0;

    public static final Logger log = LoggerFactory.getLogger("DATASET LOG");
    public static String getScienceName(CSVRecord record){return record.get(E.Science_name);}
    public static String[] getCommonName(CSVRecord record){return record.get(E.Common_names).split(",");}

    public static Map<CSVRecord,  Integer> getRecords(UserVariable userVariable){
        Map<CSVRecord, Integer> map = new HashMap<>();
        Set<? extends Parameters> parameters = userVariable.getParameters();

        for (CSVRecord record : getRecords()) {
            explored_fields++;
            int score = 0;
            boolean flag = false;
            for (Parameters parameter : parameters) {
                Map<String, String> par_ = parameter.getParameters();
                for (String col : par_.keySet()) {
                    final String var = par_.get(col);
                    if(var==null) continue;
                    float float_val = Float.MAX_VALUE;
                    if(var.equals("NA")) continue;
                    try {
                        float_val = Float.parseFloat(var);
                    } catch (NumberFormatException ignored) {}

                    switch (col){
                        case "LAT" -> {
//                            if(true) continue;
                            if(
                                    record.get(E.O_minimum_latitude).equals("NA") ||
                                    record.get(E.O_maximum_latitude).equals("NA")
                            ) continue;
                            float min = Float.parseFloat(record.get(E.O_minimum_latitude));
                            float max = Float.parseFloat(record.get(E.O_maximum_latitude));
                            if((min <= float_val && max >= float_val)){
                                score += 1;
                                flag = true;
                            }
                            else score += (int) (float_val < min ? float_val - min :
                                    max - float_val);

                            if(
                                    record.get(E.A_minimum_latitude).equals("NA") ||
                                    record.get(E.A_maximum_latitude).equals("NA")
                            ) continue;
                            float amin = Float.parseFloat(record.get(E.A_minimum_latitude));
                            float amax = Float.parseFloat(record.get(E.A_maximum_latitude));
                            if (amin <= float_val && amax >= float_val){
                                score += 1;
                                flag = true;
                            }
                        }
                        case "ALT" -> {
                            if(record.get(E.O_maximum_altitude).equals("NA")) continue;
                            float altitude = Float.parseFloat(record.get(E.O_maximum_altitude));
                            if(altitude >= float_val){
                                score += 1;
                                flag = true;
                            }
                            else score -= (int) Math.abs(altitude - float_val);
                        }
                        case "RAIN" -> {
//                            if(true) continue;
                            if(record.get(E.O_minimum_rainfall).equals("NA") || record.get(E.O_maximum_rainfall).equals("NA")) continue;
                            float min = Float.parseFloat(record.get(E.O_minimum_rainfall));
                            float max = Float.parseFloat(record.get(E.O_maximum_rainfall));
                            if (min <= float_val && max >= float_val) {
                                score += 1;
                                flag = true;
                            }
                            else score += (int) // minus 1 per rainfall yang diluar jangkauan
                                    (float_val < min ? float_val - min : max - float_val);
                        }
                        case "TEMP" -> {
                            if(record.get(E.O_minimum_temperature).equals("NA")
                                    || record.get(E.O_maximum_temperature).equals("NA")) continue;
                            float min = Float.parseFloat(record.get(E.O_minimum_temperature));
                            float max = Float.parseFloat(record.get(E.O_maximum_temperature));
                            if (min <= float_val && max >= float_val) {
                                score += 1;
                                flag = true;
                            }
                            else score -= (int) // minus 1 per temperatur yang diluar jangkauan
                                    (float_val < min ? Math.abs(float_val - min) : Math.abs(max - float_val));
                        }

                        case "PANEN" -> {
                            if(record.get(E.MIN_crop_cycle).equals("NA") || record.get(E.MAX_crop_cycle).equals("NA")) continue;
                            float min = Float.parseFloat(record.get(E.MIN_crop_cycle));
                            float max = Float.parseFloat(record.get(E.MAX_crop_cycle));
                            if (min <= float_val && max >= float_val) {
                                ++score;
                                flag = true;
                            }
                        }

                        case "QUERY" -> {
                            if (record.get(E.Common_names).contains(var)) {
                                score++;
                                flag = true;
                            }
                        }

                        case "PH" -> {
                            if(record.get(E.O_minimum_ph).equals("NA") || record.get(E.A_minimum_ph).equals("NA")) continue;
                            float min = Float.parseFloat(record.get(E.O_minimum_ph));
                            float max = Float.parseFloat(record.get(E.O_maximum_ph));
                            if (min <= float_val && max >= float_val) {
                                score += 1;
                                flag = true;
                            }
                            else score += (int) // minus n per ph yang diluar jangkauan
                                    (float_val < min ? float_val - min : max - float_val);
                        }

                        default -> {
                            String row = record.get(col);
                            if(row.contains(var)){
                                if(col.equals(E.Climate_zone)) score += 3;
                                else score += 2;
                                flag = true;
                            }
                            else if(col.equals(E.Climate_zone)) {
                                score -= 354;
                                flag = false;
                            } else if(col.equals(E.O_soil_drainage)){
                                score -= 9;
                                flag = false;
                            } else if(col.equals(E.Category)) {
                                score -= 12;
                            }
                        }
                    }
                }
            }
            if(flag && score > 0) map.put(record, score);
            if(record.get(E.Science_name).contains("Oryza")){
                log.info("{}, score: {}", record.get(E.Science_name),  score);
            }
        }
        return map;
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
