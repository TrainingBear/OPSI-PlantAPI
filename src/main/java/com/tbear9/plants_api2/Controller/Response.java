package com.tbear9.plants_api2.Controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class Response implements Serializable {
    private String soilName;
    private List<Map<String, String>> perawatan;
    @Singular private final List<Map<String, List<String>>> disease = new ArrayList<>();
}
