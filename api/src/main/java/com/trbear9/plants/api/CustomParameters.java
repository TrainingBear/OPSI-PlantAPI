package com.trbear9.plants.api;

import com.trbear9.plants.E;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomParameters implements Parameters {
    E.CATEGORY category;
    E.LIFESPAM lifeSpan;
    String query;
    int panen;
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
