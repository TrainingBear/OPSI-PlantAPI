package com.tbear9.plants_api2;

import lombok.Builder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
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
            for (Parameters parameter : parameters)
                for (String col : parameter.getParameters().keySet()) {
                    String row = record.get(col);
                    if(row.contains(parameter.getParameters().get(col))){
                        score += 1;
                        flag = true;
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

    public interface Parameters extends Serializable {
        Map<String, String> getParameters();
    }

    @Builder
    public static class SoilParameters implements Parameters {
        public final E.DEPTH O_depth;
        public final E.DEPTH A_depth;
        public final E.TEXTURE O_texture;
        public final E.TEXTURE A_texture;
        public final E.FERTILITY O_fertility; // tingkat kesuburan
        public final E.FERTILITY A_fertility;
        public final E.DRAINAGE O_drainage;
        public final E.DRAINAGE A_drainage;
        public final int pH;

        @Override
        public Map<String, String> getParameters() {
            Map<String, String> map = new HashMap<>();
            map.put(E.O_soil_depth, O_depth== null? null : O_depth.head);
            map.put(E.A_soil_depth, A_depth== null? null : A_depth.head);
            map.put(E.O_soil_texture, O_texture== null? null : O_texture.head);
            map.put(E.A_soil_texture, A_texture== null? null : A_texture.head);
            map.put(E.O_soil_fertility, O_fertility== null? null : O_fertility.head);
            map.put(E.A_soil_fertility, A_fertility== null? null : A_fertility.head);
            map.put(E.O_soil_drainage, O_drainage== null? null : O_drainage.head);
            map.put(E.A_soil_drainage, A_drainage== null? null : A_drainage.head);
            map.put("PH", String.valueOf(pH));

            return map;
        }
    }

    @Builder
    public static class GeoParameters implements Parameters {
        private final E.CLIMATE iklim;
        private final int latitude;
        private final int longitude;
        private final int altitude;
        private final int rainfall;
        private final int temperature;

        @Override
        public Map<String, String> getParameters() {
            Map<String, String> map = new HashMap<>();
            map.put(E.Climate_zone, iklim == null? null : iklim.head);
            map.put("LAT", String.valueOf(latitude));
            map.put("LONG", String.valueOf(longitude));
            map.put("ALT", String.valueOf(altitude));
            map.put("RAIN", String.valueOf(rainfall));
            map.put("TEMP", String.valueOf(temperature));
            return map;
        }
    }

    @Builder
    public static class UserParameters implements Parameters {
        private final E.CATEGORY category;
        private final E.LIFESPAM lifeSpan;
        private final String query;
        private final int panen;
        @Override
        public Map<String, String> getParameters() {
            Map<String, String> map = new HashMap<>();
            map.put(E.Category, category == null ? null : category.head);
            map.put(E.Life_span, lifeSpan == null ? null : lifeSpan.head);
            map.put("PANEN", String.valueOf(panen));
            map.put("QUERY", query);
            return map;
        }
    }
}
