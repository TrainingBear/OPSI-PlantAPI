package com.tbear9.plants_api;

public enum Table {
    PLANT_LIST("plant_list"),
    PLANT_DISEASE_LIST("plant_disease_list"),
    PLANT_DETAILS("plant_details"),
    PLANT_GUIDE_LIST("plant_guide_list"),
    PLANT_GUIDE_DETAILS("plant_guide_details"),
    PLANT_HARDINESS("plant_hardiness");

    final String name;
    Table(String name){
        this.name = name;
    }
}
