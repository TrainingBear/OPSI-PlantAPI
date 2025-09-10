package com.trbear9.plants.api;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVariable {
    private byte[] image;
    private String tanah;
    @Singular private Set<Parameters> parameters;
    public void modify(SoilParameters par){
        for (Parameters parameter : parameters) {
            if(parameter instanceof SoilParameters){
                ((SoilParameters) parameter).modify(par);
            }
        }
    }
}
