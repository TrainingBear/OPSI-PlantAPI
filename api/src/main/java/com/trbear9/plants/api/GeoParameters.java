package com.trbear9.plants.api;

import com.trbear9.plants.E;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
    public class GeoParameters implements Parameters {
        private final E.CLIMATE iklim;
        private final int latitude;
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