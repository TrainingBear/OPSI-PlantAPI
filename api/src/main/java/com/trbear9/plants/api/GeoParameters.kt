package com.trbear9.plants.api

import com.trbear9.plants.E
import com.trbear9.plants.E.CLIMATE
import lombok.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class GeoParameters : Parameters {
    var iklim: CLIMATE? = CLIMATE.tropical_wet_and_dry
    var latitude: Double = -7.2565293 // di sekitar ambarawa (default)
    var longtitude: Double = 110.402824 // di sekitar ambarawa (default)
    var altitude: Double = 0.0
    var rainfall: Double = 2000.0 // di jawa tengah (default)
    var min: Double = 18.0
    var max: Double = 31.0 // di seluruh indonesia (default)
    override fun getParameters(): MutableMap<String?, String?> {
        val map: MutableMap<String?, String?> = HashMap()
        map.put(E.Climate_zone, iklim?.head)
        map.put("LAT", abs(latitude).toString())
        map.put("ALT", altitude.toString())
        map.put("RAIN", rainfall.toString())
        map.put("TEMPMIN", min(min, max).toString())
        map.put("TEMPMAX", max(min, max).toString())
        return map
    }
}