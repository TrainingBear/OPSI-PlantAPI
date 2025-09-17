package com.trbear9.plants.api;

import com.trbear9.plants.E;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoilParameters implements Parameters {
        /// sources:
        /// - https://www.sciencedirect.com/topics/agricultural-and-biological-sciences/alluvial-soil
        /// - https://amoghavarshaiaskas.in/alluvial-soil/
        public static final SoilParameters ALLUVIAL = SoilParameters.builder()
                .texture(E.TEXTURE.medium)
                .fertility(E.FERTILITY.high)
                .drainage(E.DRAINAGE.well)
                .pH(6f)
                .build();

        /// sources:
        /// - [wikipedia](https://en.wikipedia.org/wiki/Laterite#Agriculture)
        /// - JURNAL KINGDOM The Journal of Biological Studies
        /// Volume 9 No 2, Agustus, 2023, 131-137
        /// https://journal.student.uny.ac.id/
        ///
        public static final SoilParameters LATERITE = SoilParameters.builder()
                .fertility(E.FERTILITY.low)
                .texture(E.TEXTURE.heavy)
                .drainage(E.DRAINAGE.well)
                .pH(5.5f)
                .build();

//        Humus
//        ph Tanah: pH berkisar 6-7
//        tekstur tanah: Tekstur gembur dan tidak padat
//        kesuburan: Tergantung keasaman pH nak berkisar 6-7 berarti masih subur dan ideal
//        Drainase: Baik (Well)
        public static final SoilParameters HUMUS = SoilParameters.builder()
                .fertility(E.FERTILITY.moderate)
                .texture(E.TEXTURE.organic)
                .drainage(E.DRAINAGE.well)
                .pH(6f)
                .build();
//        Inceptisol
//        ph Tanah: pH normal berkisar 5,0-7,0
//        Sumber: UGM.ac.id
//        Tekstur tanah: debu, berdebu, lempung, lempung berdebu
//        Kesuburan: Sedang hingga tinggi (sedang)
//        Drainase: Karena termasuk tanah muda, drainase alamiahnya tergolong jelek
        public static final SoilParameters INCEPTISOL = SoilParameters.builder()
                .fertility(E.FERTILITY.moderate)
                .texture(E.TEXTURE.heavy)
                .drainage(E.DRAINAGE.poorly)
                .pH(5.9f)
                .build();
//        Tanah kapur (rendzina)
//        pH tanah: sangat basa bisa berkisar 6,0-8,0 bahkan bisa sampai 8,4
//        Tekstur tanah: Tekstur lempung seperti vertisol, (bisa juga lempung berdebu)
//        Kesuburan: Rendah
//        Drainase: Tidak terlalu baik
        public static final SoilParameters KAPUR = SoilParameters.builder()
                .fertility(E.FERTILITY.low)
                .texture(E.TEXTURE.light)
                .drainage(E.DRAINAGE.poorly)
                .pH(7f)
                .build();
//        Tanah Pasir (Berpasir)
//        pH tanah: Normalnya pH = 7
//        pH < 7 = asam
//        pH > 7 = Basa
//        Kesuburan: Kurang Subur
//        Drainase: Tanah yang lebih banyak pasir mempunyai drainase yang baik
//        Tekstur: Kasar
        public static final SoilParameters PASIR = SoilParameters.builder()
                .fertility(E.FERTILITY.low)
                .texture(E.TEXTURE.light)
                .drainage(E.DRAINAGE.excessive)
                .pH(7f)
                .build();
//        Andosol
//        ph Tanah: pH =  5.4
//        Drainase tanah andisol secara umum cenderung baik
//        Tekstur Tanah Andosol:  tekstur tanah sedang
        public static final SoilParameters ANDOSOL = SoilParameters.builder()
                .fertility(E.FERTILITY.high)
                .texture(E.TEXTURE.wide)
                .drainage(E.DRAINAGE.well)
                .pH(5.4f)
                .build();

//        Entisol
//        ph Tanah: 6,36-7,41
//        texture: Kasar
//        drainage: Well (baik)
//        kesuburan: tergolong rendah
        public static final SoilParameters ENTISOL = SoilParameters.builder()
        .fertility(E.FERTILITY.high)
        .texture(E.TEXTURE.medium)
        .drainage(E.DRAINAGE.well)
        .pH(5.4f)
        .build();

        public E.DEPTH depth;
        public E.TEXTURE texture;
        public E.FERTILITY fertility; // tingkat kesuburan
        public E.DRAINAGE drainage;
        public float pH;

        public void modify(SoilParameters soil){
            texture = soil.texture;
            fertility = soil.fertility;
            drainage = soil.drainage;
            pH = soil.pH;
        }

        @Override
        public Map<String, String> getParameters() {
            Map<String, String> map = new HashMap<>();
            map.put(E.O_soil_depth, depth == null? null : depth.head);
            map.put(E.O_soil_texture, texture == null? null : texture.head);
            map.put(E.O_soil_fertility, fertility == null? null : fertility.head);
            map.put(E.O_soil_drainage, drainage == null? null : drainage.head);
            map.put("PH", String.valueOf(pH));

            return map;
        }
    }
