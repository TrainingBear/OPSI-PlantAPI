package com.trbear9.plants.api.blob;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductSystem implements Serializable {
    private String rumah_tangga;
    private String komersial;
    private String industri;
}

