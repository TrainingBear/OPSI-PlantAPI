package com.trbear9.plants_api;

public enum Query {
    COMMON_NAME("common_name", 1);
    public final String path;
    Query(String path, int path_level){
        this.path = path;
    }
}
