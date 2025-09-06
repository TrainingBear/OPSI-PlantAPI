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
                .O_texture(E.TEXTURE.medium)
                .O_fertility(E.FERTILITY.high)
                .O_drainage(E.DRAINAGE.well)
                .pH(6f)
                .build();

        /// sources:
        /// - [wikipedia](https://en.wikipedia.org/wiki/Laterite#Agriculture)
        /// - JURNAL KINGDOM The Journal of Biological Studies
        /// Volume 9 No 2, Agustus, 2023, 131-137
        /// https://journal.student.uny.ac.id/
        ///
        public static final SoilParameters LATERITE = SoilParameters.builder()
                .O_fertility(E.FERTILITY.low)
                .O_texture(E.TEXTURE.heavy)
                .O_drainage(E.DRAINAGE.well)
                .pH(5.5f)
                .build();

//        Humus
//        ph Tanah: pH berkisar 6-7
//        tekstur tanah: Tekstur gembur dan tidak padat
//        kesuburan: Tergantung keasaman pH nak berkisar 6-7 berarti masih subur dan ideal
//        Drainase: Baik (Well)
        public static final SoilParameters HUMUS = SoilParameters.builder()
                .O_fertility(E.FERTILITY.moderate)
                .O_texture(E.TEXTURE.organic)
                .O_drainage(E.DRAINAGE.well)
                .pH(6f)
                .build();
//        Inceptisol
//        ph Tanah: pH normal berkisar 5,0-7,0
//        Sumber: UGM.ac.id
//        Tekstur tanah: debu, berdebu, lempung, lempung berdebu
//        Kesuburan: Sedang hingga tinggi (sedang)
//        Drainase: Karena termasuk tanah muda, drainase alamiahnya tergolong jelek
        public static final SoilParameters INCEPTISOL = SoilParameters.builder()
                .O_fertility(E.FERTILITY.moderate)
                .O_texture(E.TEXTURE.heavy)
                .O_drainage(E.DRAINAGE.poorly)
                .pH(5.9f)
                .build();
//        Tanah kapur (rendzina)
//        pH tanah: sangat basa bisa berkisar 6,0-8,0 bahkan bisa sampai 8,4
//        Tekstur tanah: Tekstur lempung seperti vertisol, (bisa juga lempung berdebu)
//        Kesuburan: Rendah
//        Drainase: Tidak terlalu baik
        public static final SoilParameters KAPUR = SoilParameters.builder()
                .O_fertility(E.FERTILITY.low)
                .O_texture(E.TEXTURE.light)
                .O_drainage(E.DRAINAGE.poorly)
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
                .O_fertility(E.FERTILITY.low)
                .O_texture(E.TEXTURE.light)
                .O_drainage(E.DRAINAGE.excessive)
                .pH(7f)
                .build();
//        Andosol
//        ph Tanah: pH =  5.4
//        Drainase tanah andisol secara umum cenderung baik
//        Tekstur Tanah Andosol:  tekstur tanah sedang
        public static final SoilParameters ANDOSOL = SoilParameters.builder()
                .O_fertility(E.FERTILITY.high)
                .O_texture(E.TEXTURE.wide)
                .O_drainage(E.DRAINAGE.well)
                .pH(5.4f)
                .build();

//        Entisol
//        ph Tanah: 6,36-7,41
//        texture: Kasar
//        drainage: Well (baik)
//        kesuburan: tergolong rendah
        public static final SoilParameters ENTISOL = SoilParameters.builder()
        .O_fertility(E.FERTILITY.high)
        .O_texture(E.TEXTURE.medium)
        .O_drainage(E.DRAINAGE.well)
        .pH(5.4f)
        .build();

        public E.DEPTH O_depth;
        public E.TEXTURE O_texture;
        public E.FERTILITY O_fertility; // tingkat kesuburan
        public E.DRAINAGE O_drainage;
        public float pH;

        public void modify(SoilParameters soil){
            O_texture = soil.O_texture;
            O_fertility = soil.O_fertility;
            O_drainage = soil.O_drainage;
            pH = soil.pH;
        }

        @Override
        public Map<String, String> getParameters() {
            Map<String, String> map = new HashMap<>();
            map.put(E.O_soil_depth, O_depth== null? null : O_depth.head);
            map.put(E.O_soil_texture, O_texture== null? null : O_texture.head);
            map.put(E.O_soil_fertility, O_fertility== null? null : O_fertility.head);
            map.put(E.O_soil_drainage, O_drainage== null? null : O_drainage.head);
            map.put("PH", String.valueOf(pH));

            return map;
        }
    }
