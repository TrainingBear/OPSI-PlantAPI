package com.tbear9.plants_api2.Controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Set;

@Getter
@Builder
public class Response {
    String name;
    String perawatan;
    @Singular Set<String> penyakit;
}
