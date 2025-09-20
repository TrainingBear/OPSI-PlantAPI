package com.trbear9.plants.api;

import com.trbear9.plants.E;
import kotlin.math.MathKt;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoParameters implements Parameters {
    public E.CLIMATE iklim = E.CLIMATE.tropical_wet_and_dry;
    public double latitude = -7.2565293f, // di sekitar ambarawa (default)
            longtitude = 110.402824f, // di sekitar ambarawa (default)
            altitude, elevation,
            rainfall = 3500, // di jawa tengah (default)
            min = 19, max = 31; // di seluruh indonesia (default)
    @Override
    public Map<String, String> getParameters() {
        Map<String, String> map = new HashMap<>();
        map.put(E.Climate_zone, iklim == null ? null : iklim.head);
        map.put("LAT", String.valueOf(Math.abs(latitude)));
        map.put("ALT", String.valueOf(altitude));
        map.put("RAIN", String.valueOf(rainfall));
        map.put("TEMPMIN", String.valueOf(Math.min(min, max)));
        map.put("TEMPMAX", String.valueOf(Math.max(min, max)));
        return map;
    }
}