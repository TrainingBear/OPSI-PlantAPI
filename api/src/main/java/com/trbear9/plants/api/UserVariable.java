package com.trbear9.plants.api;

import lombok.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserVariable {
    @Setter private String tanah;
    @Singular private final Map<Class<? extends Parameters>, Parameters> parameters = new HashMap<>();
    @Setter private byte[] image;
    private String hash;

    public void modify(SoilParameters par){
        ((SoilParameters) parameters.get(SoilParameters.class)).modify(par);
    }

    public void add(Parameters... parms){
        for (Parameters par : parms)
            parameters.put(par.getClass(), par);
    }

    @SneakyThrows
    public void computeHash() {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        if (image != null) digest.update(image);
        if (tanah != null) digest.update(tanah.getBytes(StandardCharsets.UTF_8));

        for (Map.Entry<Class<? extends Parameters>, Parameters> entry : parameters.entrySet()) {
            digest.update(entry.getKey().getName().getBytes());
            Parameters parameter = entry.getValue();
            digest.update(parameter.toString().getBytes());
            for (Map.Entry<String, String> par : parameter.getParameters().entrySet()) {
                digest.update(par.getKey().getBytes());
                String value = par.getValue();
                digest.update(value==null? parameter.toString().getBytes() : value.getBytes());
            }
        }

        byte[] hashBytes = digest.digest();
        hash = Base64.getEncoder().encodeToString(hashBytes);
    }

    @Override
    public int hashCode(){
        return hash.hashCode();
    }
}
