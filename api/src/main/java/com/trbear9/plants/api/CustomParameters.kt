package com.trbear9.plants.api

import com.trbear9.plants.E
import com.trbear9.plants.E.CATEGORY
import com.trbear9.plants.E.LIFESPAM
import lombok.*

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CustomParameters : Parameters {
    var category: CATEGORY? = null
    var lifeSpan: LIFESPAM? = null
    var query: String? = null
    var panen: Int? = null
    override fun getParameters(): MutableMap<String?, String?> {
        val map: MutableMap<String?, String?> = HashMap()
        map.put(E.Category, category?.head)
        map.put(E.Life_span, lifeSpan?.head)
        map.put("PANEN", panen?.toString())
        map.put("QUERY", query)
        return map
    }
}
