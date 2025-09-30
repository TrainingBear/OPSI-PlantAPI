package com.trbear9.plants;

public final class E {
    public final static String[] label = {
           "Aluvial",
           "Andosol",
           "Humus",
           "Kapur",
           "Laterit",
           "Pasir"
    };
    public static final String PERAWATAN = "PERAWATAN";
    public static final String NAME = "NAME";
    public static final String PENYAKIT = "PENYAKIT";

    public static final String Science_name = "ScientificName";
    public static final String Authority = "AUTH";
    public static final String Family = "FAMNAME";
    public static final String Synonyms = "SYNO";
    public static final String Common_names = "COMNAME";
    public static final String Life_form = "LIFO";
    public static final String Habitat = "HABI";
    public static final String Life_span = "LISPA";
    public static final String Physiology = "PHYS";
    public static final String Category = "CAT";
    public static final String Plant_attributes = "PLAT";
    public static final String O_minimum_temperature = "TOPMN";
    public static final String O_maximum_temperature = "TOPMX";
    public static final String A_minimum_temperature = "TMIN";
    public static final String A_maximum_temperature = "TMAX";
    public static final String O_minimum_rainfall = "ROPMN";
    public static final String O_maximum_rainfall = "ROPMX";
    public static final String A_minimum_rainfall = "RMIN";
    public static final String A_maximum_rainfall = "RMAX";
    public static final String O_minimum_ph = "PHOPMN";
    public static final String O_maximum_ph = "PHOPMX";
    public static final String A_minimum_ph = "PHMIN";
    public static final String A_maximum_ph = "PHMAX";
    public static final String O_minimum_latitude = "LATOPMN";
    public static final String O_maximum_latitude = "LATOPMX";
    public static final String A_minimum_latitude = "LATMN";
    public static final String A_maximum_latitude = "LATMX";
    public static final String O_maximum_altitude = "ALTMX";
    public static final String O_minimum_light_intentsity = "LIOPMN";
    public static final String O_maximum_light_intentsity = "LIOPMX";
    public static final String A_minimum_light_intensity = "LIMN";
    public static final String A_maximum_light_intensity = "LIMX";
    public static final String O_soil_depth = "DEP";
    public static final String A_soil_depth = "DEPR";
    public static final String O_soil_texture = "TEXT";
    public static final String A_soil_texture = "TEXTR";
    public static final String O_soil_fertility = "FER";
    public static final String A_soil_fertility = "FERR";
    public static final String O_soil_toxicity = "TOX";
    public static final String A_soil_toxicity = "TOXR";
    public static final String O_soil_salinity = "SAL";
    public static final String A_soil_salinity = "SALR";
    public static final String O_soil_drainage = "DRA";
    public static final String A_soil_drainage = "DRAR";
    public static final String Killing_temperature_during_rest = "KTMPR";
    public static final String Killing_temperature_early_growth = "KTMP";
    public static final String Photoperiod = "PHOTO";
    public static final String Climate_zone = "CLIZ";
    public static final String Abiotic_tolerance = "ABITOL";
    public static final String Abiotic_susceptibility = "ABISUS";
    public static final String Introduction_risk = "INTRI";
    public static final String Production_system = "PROSY";
    public static final String MIN_crop_cycle = "GMIN";
    public static final String MAX_crop_cycle = "GMAX";

    public static final String PORT = "EcoPortCode";

    public enum LIFESPAM{
        ephemeral("ephemeral"),
        annual("annual"),
        perennial("perennial"),
        biennial("biennial");
        public static final String optimal = Life_span;
        public final String head;
        LIFESPAM(String string){
            this.head = string;
        }
    }

    public enum CATEGORY{
        other("other"),
        cereals_pseudocereals("cereals & pseudocereals"),
        pulls("pulses (grain legumes)"),
        roots_tubers("roots/tubers"),
        forage_pastures("forage/pasture"),
        fruit_nut("fruits & nuts"),
        vegetables("vegetables"),
        materials("materials"),
        ornamentals_turf("ornamentals/turf"),
        medicinals_and_armoatic("medicinals & aromatic"),
        forest_or_wood("forest/wood"),
        cover_crop("cover crop"),
        environmental("environmental"),
        weed("weed");
        public static final String optimal = Category;
        public final String head;
        CATEGORY(String string){
            this.head = string;
        }
    }
    public enum PLANT_ATTRIBUTE{
        other("other"),
        grown_on_large_scale("grown on large scale"),
        grown_on_small_scale("grown on small scale"),
        harvested_from_wild("harvested from wild"),
        previously_widely_grown("previously widely grown"),
        ;
        public static final String optimal = Plant_attributes;
        public final String head;
        PLANT_ATTRIBUTE(String string){
            this.head = string;
        }
    }

    public enum DEPTH{
        verry_shallow("verry shallow"),
        shallow("shallow"),
        deep("deep"),
        medium("medium"),
        ;
        public static final String absolute = A_soil_depth;
        public static final String optimal = O_soil_depth;
        public final String head;
        DEPTH(String string){
            this.head = string;
        }
    }
    public enum TEXTURE{
        heavy("heavy"),
        medium("medium"),
        light("light"),
        wide("wide"),
        organic("organic")
        ;
        public static final String absolute = A_soil_texture;
        public static final String optimal = O_soil_texture;
        public final String head;
        TEXTURE(String string){
            this.head = string;
        }
    }
    public enum FERTILITY{
        low("low"),
        moderate("moderate"),
        high("high");
        ;
         public static final String absolute = A_soil_fertility;
         public static final String optimal = O_soil_fertility;
         public final String head;
         FERTILITY(String string){
             this.head = string;
         }
    }

    public enum SALINITY{
        none("none"),
        low("low"),
        moderate("moderate"),
        high("high");
        ;
         public static final String absolute = A_soil_salinity;
         public static final String optimal = O_soil_salinity;
         public final String head;
         SALINITY(String string){
             this.head = string;
         }
    }

    public enum DRAINAGE{
        poorly("poorly"),
        well("well"),
        excessive("excessive")
        ;
        public static final String absolute = A_soil_drainage;
        public static final String optimal = O_soil_drainage;
        public final String head;
        DRAINAGE(String string){
             this.head = string;
         }
    }

    public enum CLIMATE{
        tropical_wet_and_dry("tropical wet & dry"),
        tropical_wet("tropical wet"),
        desert_or_arid("desert or arid"),
        steppe_or_semiarid("steppe or semiarid"),
        subtropical_dry_summer("subtropical dry summer"),
        subtropical_dry_winter("subtropical dry winter"),
        temperate_oceanic("temperate oceanic"),
        temperate_continental("temperate continental"),
        temperate_with_humid_winters("temperate with humid winters"),
        temperate_with_dry_winters("temperate with dry winters"),
        boreal("boreal"),
        polar("polar"),
        ;
        public static final String optimal = Climate_zone;
        public final String head;
        CLIMATE(String string){
             this.head = string;
         }
    }

    public enum PHOTOPERIOD{
        short_day("short day"),
        long_day("long day"),
        neutral_day("neutral day"),
        sensitive("sensitive")
        ;
        public static final String optimal = Photoperiod;
        public final String head;
        PHOTOPERIOD(String string){
             this.head = string;
         }
    }
    public enum HABIT{
        erect("erect"),
        prostrate_procumbent_semierect("prostrate/procumbent/semi-erect"),
        climber_scrambler_scadent("climber/scrambler/scadent"),
        acaulescent_or_rosette_plants("acaulescent(or rosette plants)"),
        ;
        public static final String optimal = Habitat;
        public final String head;
        HABIT(String string){
             this.head = string;
         }
    }

    public enum LIFEFORM{
        ;
        public static final String optimal = Life_form;
        public final String head;
        LIFEFORM(String string){
             this.head = string;
         }
    }

    public enum LIGHT{
        heavy_shade("heavy shade"),
        light_shade("light shade"),
        cloudy_skies("cloudy skies"),
        clear_skies("clear skies"),
        very_bright("very bright")
        ;
        public static final String optimal_min = O_minimum_light_intentsity;
        public static final String optimal_max = O_maximum_light_intentsity;
        public static final String absolute_min = A_minimum_light_intensity;
        public static final String absolute_max = A_maximum_light_intensity;
        public final String head;
        LIGHT(String string){
             this.head = string;
         }
    }
}
