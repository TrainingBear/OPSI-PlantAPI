package com.trbear9.plants.api;

import com.trbear9.plants.E;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Builder
public record CustomParameters(E.CATEGORY category, E.LIFESPAM lifeSpan, String query,
                               int panen) implements Parameters {
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
