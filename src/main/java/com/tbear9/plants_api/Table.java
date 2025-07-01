package com.tbear9.plants_api;

@SuppressWarnings("JANGAN DI MODIFIKASI YANG SUDAH ADA")
public enum Table {
    PLANT_LIST("plant_list"),
    PLANT_DISEASE_LIST("plant_disease_list"),
    PLANT_GUIDE_LIST("plant_guide_list"),
    PLANT_DETAILS("plant_details"),
    PLANT_HARDINESS("plant_hardiness");

    final String name;
    Table(String name){
        this.name = name;
    }

    @SuppressWarnings("")
    public String getUrl(String api, String parameter){
        return switch (this){
            case PLANT_LIST -> "https://perenual.com/api/v2/species-list?key="+api+parameter;
            case PLANT_DISEASE_LIST -> "https://perenual.com/api/pest-disease-list?key="+api+parameter;
            case PLANT_GUIDE_LIST -> "https://perenual.com/api/species-care-guide-list?key="+api+parameter;
            case PLANT_DETAILS -> "https://perenual.com/api/v2/species/details/"+parameter+"?key="+api;
            case PLANT_HARDINESS -> "https://perenual.com/api/hardiness-map?species_id="+parameter+"&key="+api;
        };
    }
}
