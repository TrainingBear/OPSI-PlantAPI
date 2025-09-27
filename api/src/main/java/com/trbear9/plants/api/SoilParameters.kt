package com.trbear9.plants.api

import com.trbear9.plants.E
import com.trbear9.plants.E.*
import lombok.*

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SoilParameters : Parameters {
    var depth: DEPTH? = null
    var texture: TEXTURE? = null
    var fertility: FERTILITY? = null // tingkat kesuburan
    var drainage: DRAINAGE? = null
    var pH: Float? = null

    fun modify(soil: SoilParameters) {
        texture = soil.texture
        fertility = soil.fertility
        drainage = soil.drainage
        pH = soil.pH
    }

    override fun getParameters(): MutableMap<String?, String?> {
        val map: MutableMap<String?, String?> = HashMap()
        map.put(O_soil_depth, depth?.head)
        map.put(O_soil_texture, texture?.head)
        map.put(O_soil_fertility, fertility?.head)
        map.put(O_soil_drainage, drainage?.head)
        map.put("PH", pH.toString())

        return map
    }

    companion object {
        /** sources:
         * - https://www.sciencedirect.com/topics/agricultural-and-biological-sciences/alluvial-soil
         * - https://amoghavarshaiaskas.in/alluvial-soil/ */
        @JvmField
        val ALLUVIAL = SoilParameters().apply {
            texture = (TEXTURE.medium)
            fertility = (FERTILITY.high)
            drainage = (DRAINAGE.well)
            pH= (6f)
        }

        /** sources:
         * - [wikipedia](https://en.wikipedia.org/wiki/Laterite#Agriculture)
         * - JURNAL KINGDOM The Journal of Biological Studies
         * Volume 9 No 2, Agustus, 2023, 131-137
         * https://journal.student.uny.ac.id/
         */
        @JvmField
        val LATERITE: SoilParameters? = SoilParameters().apply {
            fertility = (FERTILITY.low)
            texture = (TEXTURE.heavy)
            drainage = (DRAINAGE.well)
            pH = (5.5f)
        }

        //        Humus
        //        ph Tanah: pH berkisar 6-7
        //        tekstur tanah: Tekstur gembur dan tidak padat
        //        kesuburan: Tergantung keasaman pH nak berkisar 6-7 berarti masih subur dan ideal
        //        Drainase: Baik (Well)
        @JvmField
        val HUMUS: SoilParameters? = SoilParameters().apply {
            fertility = (FERTILITY.moderate)
            texture = (TEXTURE.organic)
            drainage = (DRAINAGE.well)
            pH = (6f)
        }

        //        Inceptisol
        //        ph Tanah: pH normal berkisar 5,0-7,0
        //        Sumber: UGM.ac.id
        //        Tekstur tanah: debu, berdebu, lempung, lempung berdebu
        //        Kesuburan: Sedang hingga tinggi (sedang)
        //        Drainase: Karena termasuk tanah muda, drainase alamiahnya tergolong jelek
        @JvmField
        val INCEPTISOL: SoilParameters? = SoilParameters().apply {
            fertility = (FERTILITY.moderate)
            texture = (TEXTURE.heavy)
            drainage = (DRAINAGE.poorly)
            pH = (5.9f)
        }

        //        Tanah kapur (rendzina)
        //        pH tanah: sangat basa bisa berkisar 6,0-8,0 bahkan bisa sampai 8,4
        //        Tekstur tanah: Tekstur lempung seperti vertisol, (bisa juga lempung berdebu)
        //        Kesuburan: Rendah
        //        Drainase: Tidak terlalu baik
        @JvmField
        val KAPUR: SoilParameters? = SoilParameters().apply {
            fertility = (FERTILITY.low)
            texture = (TEXTURE.light)
            drainage = (DRAINAGE.poorly)
            pH = (7f)
        }

        //        Tanah Pasir (Berpasir)
        //        pH tanah: Normalnya pH = 7
        //        pH < 7 = asam
        //        pH > 7 = Basa
        //        Kesuburan: Kurang Subur
        //        Drainase: Tanah yang lebih banyak pasir mempunyai drainase yang baik
        //        Tekstur: Kasar
        @JvmField
        val PASIR: SoilParameters? = SoilParameters().apply {
            fertility = (FERTILITY.low)
            texture = (TEXTURE.light)
            drainage = (DRAINAGE.excessive)
            pH = (7f)
        }

        //        Andosol
        //        ph Tanah: pH =  5.4
        //        Drainase tanah andisol secara umum cenderung baik
        //        Tekstur Tanah Andosol:  tekstur tanah sedang
        @JvmField
        val ANDOSOL: SoilParameters? = SoilParameters().apply {
            fertility = (FERTILITY.high)
            texture = (TEXTURE.wide)
            drainage = (DRAINAGE.well)
            pH = (5.4f)
        }

        //        Entisol
        //        ph Tanah: 6,36-7,41
        //        texture: Kasar
        //        drainage: Well (baik)
        //        kesuburan: tergolong rendah
        @JvmField
        val ENTISOL: SoilParameters? = SoilParameters().apply {
            fertility = (FERTILITY.high)
            texture = (TEXTURE.medium)
            drainage = (DRAINAGE.well)
            pH = (5.4f)
        }
    }
}
