package com.trbear9.plants.api;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVariable implements Serializable {
    @Serial private static final long serialVersionUID = 1L;
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
