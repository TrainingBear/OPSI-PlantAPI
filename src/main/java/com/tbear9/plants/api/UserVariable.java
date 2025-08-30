package com.tbear9.plants.api;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Builder
public class UserVariable implements Serializable {
    @Serial private static final long serialVersionUID = 1L;
    private byte[] image;
    private String tanah;
    @Singular private final Set<? extends Parameters> parameters;
    public void modify(SoilParameters par){
        for (Parameters parameter : parameters) {
            if(parameter instanceof SoilParameters){
                ((SoilParameters) parameter).modify(par);
            }
        }
    }
}
