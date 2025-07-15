package com.tbear9.plants_api2;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Getter
@Builder
public class UserVariable implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private final String tanah;
    @Singular private final Set<? extends Parameters> parameters;
}
