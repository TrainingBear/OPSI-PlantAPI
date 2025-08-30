package com.tbear9.plants.api;

import com.tbear9.plants.E;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
    public class CustomParameters implements Parameters  {
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
