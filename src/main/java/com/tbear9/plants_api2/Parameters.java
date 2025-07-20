package com.tbear9.plants_api2;

import lombok.Builder;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public interface Parameters {
    Map<String, String> getParameters();

    @Builder
    public static class SoilParameters implements Parameters {
        public static final SoilParameters LATERITE = SoilParameters.builder()
                .O_fertility(E.FERTILITY.low)
                .A_fertility(E.FERTILITY.low)
                .O_texture(E.TEXTURE.)
                .O_drainage(E.DRAINAGE.well).build();


        @Setter public E.DEPTH O_depth;
        @Setter public E.DEPTH A_depth;
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
//        private final int longitude;
        private final int altitude;
        private final int rainfall;
        private final int temperature;

        @Override
        public Map<String, String> getParameters() {
            Map<String, String> map = new HashMap<>();
            map.put(E.Climate_zone, iklim == null? null : iklim.head);
            map.put("LAT", String.valueOf(latitude));
//            map.put("LONG", String.valueOf(longitude));
            if(altitude == 0) throw new IllegalArgumentException("Altitude cannot be zero");
            map.put("ALT", String.valueOf(altitude));
            if(rainfall == 0) throw new IllegalArgumentException("Rainfall cannot be zero");
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
