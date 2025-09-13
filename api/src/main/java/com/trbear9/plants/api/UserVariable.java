package com.trbear9.plants.api;

import lombok.*;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserVariable {
    private byte[] image;
    private String tanah;
    @Singular private Map<Class<? extends Parameters>, Parameters> parameters = new HashMap<>();
    public void modify(SoilParameters par){
        ((SoilParameters) parameters.get(SoilParameters.class)).modify(par);
    }

    public void add(Parameters... parms){
        for (Parameters par : parms)
            parameters.put(par.getClass(), par);

    }
}
