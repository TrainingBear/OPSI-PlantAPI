package com.tbear9.plants_api2;

import lombok.Builder;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public interface Parameters {
    Map<String, String> getParameters();

    @Builder
    @Setter
    /// top source: [FAO](https://wrb.isric.org/files/WRB_fourth_edition_2022-12-18_errata_correction_2024-09-24.pdf)
    public static class SoilParameters implements Parameters {
        /// sources:
        /// - https://www.sciencedirect.com/topics/agricultural-and-biological-sciences/alluvial-soil
        /// - https://amoghavarshaiaskas.in/alluvial-soil/
        public static final SoilParameters ALLUVIAL = SoilParameters.builder()
                .O_texture(E.TEXTURE.medium)
                .O_fertility(E.FERTILITY.high)
                .O_drainage(E.DRAINAGE.well)
                .pH(6f)
                .build();

        /// sources:
        /// - [wikipedia](https://en.wikipedia.org/wiki/Laterite#Agriculture)
        /// - JURNAL KINGDOM The Journal of Biological Studies
        /// Volume 9 No 2, Agustus, 2023, 131-137
        /// https://journal.student.uny.ac.id/
        ///
        public static final SoilParameters LATERITE = SoilParameters.builder()
                .O_fertility(E.FERTILITY.low)
                .O_texture(E.TEXTURE.heavy)
                .O_drainage(E.DRAINAGE.well)
                .pH(5.5f)
                .build();



        public E.DEPTH O_depth;
        public E.TEXTURE O_texture;
        public E.FERTILITY O_fertility; // tingkat kesuburan
        public E.DRAINAGE O_drainage;
        public float pH;

        @Override
        public Map<String, String> getParameters() {
            Map<String, String> map = new HashMap<>();
            map.put(E.O_soil_depth, O_depth== null? null : O_depth.head);
            map.put(E.O_soil_texture, O_texture== null? null : O_texture.head);
            map.put(E.O_soil_fertility, O_fertility== null? null : O_fertility.head);
            map.put(E.O_soil_drainage, O_drainage== null? null : O_drainage.head);
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
