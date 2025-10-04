package com.trbear9.plants

import com.trbear9.plants.E.DRAINAGE
import com.trbear9.plants.api.Parameters
import com.trbear9.plants.api.UserVariable
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.math.abs
import kotlin.math.absoluteValue

object DataHandler {
    @JvmField
    var explored_fields: Int = 0
    val log: Logger = LoggerFactory.getLogger("DATASET LOG")
    val ecocropcsv: MutableList<CSVRecord>
    val perawatancsv: MutableList<CSVRecord>

    init {
        try {
            var resource = ClassPathResource("Perawatan.csv")
            resource.inputStream.use { `is` ->
                InputStreamReader(`is`).use {
                    reader -> perawatancsv = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).records
                }
            }
            log.info("Perawatan.csv loaded with size of ${perawatancsv.size}")
            resource = ClassPathResource("EcoCrop_DB.csv")
            resource.inputStream.use { `is` ->
                InputStreamReader(`is`).use {
                        reader -> ecocropcsv = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).records
                }
            }
            log.info("Loaded ECOCROP.csv with size of ${ecocropcsv.size}")
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    fun isAuthored(record: CSVRecord): Boolean {
        return record.get(E.Authority) != null || !record.get(E.Authority).isEmpty()
    }

    @JvmStatic
    fun getScienceName(record: CSVRecord?): String? {
        return record?.get(E.Science_name)
    }

    @JvmStatic
    fun commonNames(record: CSVRecord): MutableSet<String?> {
        return HashSet<String?>(
            listOf(
                *record.get(E.Common_names).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            )
        )
    }

    @JvmStatic
    fun process(data: UserVariable): MutableMap<Int, MutableSet<CSVRecord>> {
        val map: MutableMap<Int, MutableSet<CSVRecord>> =
            TreeMap<Int, MutableSet<CSVRecord>>()
        val parameters = listOf(data.geo, data.custom, data.soil)

        for (record in ecocropcsv) {
            explored_fields++
            var score = 0
            var flag = false
            for (parameter in parameters) {
                if(parameter==null) continue
                val params = parameter.getParameters()
                for (col in params.keys) {
                    val paramVal = params[col]
                    paramVal?:continue
                    var floatVar = Float.Companion.MAX_VALUE
                    try { floatVar = paramVal.toFloat() }
                    catch (_: NumberFormatException) { }

                    when (col) {
                        "LAT" -> {
                            floatVar = abs(floatVar)
                            val min: Float
                            val max: Float
                            try {
                                min = record.get(E.A_minimum_latitude).toFloat()
                                max = record.get(E.A_maximum_latitude).toFloat()
                            }catch (_: NumberFormatException) {
                                continue
                            }
                            if (floatVar in min .. max) {
                                score += 1
                                flag = true
                            }
                        }

                        "ALT" -> {
                            val altitude: Float
                            try {altitude = record.get(E.O_maximum_altitude).toFloat()}
                            catch (_: NumberFormatException) {
                                continue
                            }
                            if (altitude >= floatVar) {
                                score += 1
                                flag = true
                            } else score -= (abs(altitude - floatVar)/5).toInt()
                        }

                        "RAIN" -> {
                            val min: Float
                            val max: Float
                            try {
                                min = record.get(E.A_minimum_rainfall).toFloat()
                                max = record.get(E.A_maximum_rainfall).toFloat()
                            } catch (_: NumberFormatException) {
                                continue
                            }
                            if (floatVar in min..max) {
                                score += 1
                                flag = true
                            } else {
                                val floatVar = floatVar / 25 // bias
                                score -= (floatVar.coerceIn(
                                    Math.min(min, max),
                                    Math.max(min, max)
                                ).absoluteValue).toInt()
                            }
                        }

                        "TEMPMAX" -> {
                            val max:Float
                            try {
                                max = record.get(E.A_maximum_temperature).toFloat()
                            } catch (_: NumberFormatException){
                                continue
                            }
                            if (max >= floatVar) {
                                score += 1
                                flag = true
                            } else score -= abs(max - floatVar).toInt()
                        }

                        "TEMPMIN" -> {
                            val min:Float
                            try{
                                min = record.get(E.A_minimum_temperature).toFloat()
                            }catch (_: NumberFormatException){
                                 continue
                            }
                            if (min <= floatVar) {
                                score += 1
                                flag = true
                            } else score -= abs(floatVar - min).toInt()
                        }

                        "PANEN" -> {
                            val min:Float
                            val max:Float
                            try {
                                min = record.get(E.MIN_crop_cycle).toFloat()
                                max = record.get(E.MAX_crop_cycle).toFloat()
                            }catch (_: NumberFormatException) {
                                continue
                            }
                            if (floatVar in min..max) {
                                score+=1
                                flag = true
                            }
                        }

                        "QUERY" -> {
                            if (record.get(E.Common_names).contains(paramVal)) {
                                score++
                                flag = true
                            }
                        }

                        "PH" -> {
                            val min: Float
                            val max:Float
                            try {
                                min = record.get(E.A_minimum_ph).toFloat()
                                max = record.get(E.A_maximum_ph).toFloat()
                            } catch (_: NumberFormatException){
                                 continue
                            }
                            if (floatVar in min..max) {
                                score += 2
                                flag = true
                            }
                            else score -= (floatVar.coerceIn(Math.min(min, max), Math.max(min, max)).absoluteValue).toInt()
                        }

                        else -> {
                            val value = record.get(col)
                            if ((col == E.O_soil_texture || col == E.A_soil_texture) &&
                                record.get(col).contains("wide")
                            ) {
                                score += 3
                                flag = true
                                continue
                            }

                            if (value.contains(paramVal)) {
                                score += if (col == E.Climate_zone) 3
                                else 1
                                flag = true
                            } else if (col == E.Climate_zone) {
                                score -= 354354
                                flag = false
                            }
                        }
                    }
                }
            }
            if (isAuthored(record) && flag && score > 0)
                map.computeIfAbsent(score) { k: Int? -> HashSet<CSVRecord>() }
                .add(record)
        }
        return map
    }

    fun perawatan(record: CSVRecord): CSVRecord? {
        for (i in perawatancsv) {
            val s = i.get(record.get(E.Science_name))
            if (s != null) return i
        }
        return null
    }

    fun qperawatan(record: CSVRecord): String {
        return record.get(E.PERAWATAN)
        //        StringBuilder stringBuilder = new StringBuilder();
//        for(String s : perawatan)
//            stringBuilder.append(s).append(", ");
    }

    fun qpenyakit(record: CSVRecord): String {
        return record.get(E.PENYAKIT)
    }

    @Deprecated("")
    fun getRecord(parameters: Parameters): CSVRecord? {
        val par = parameters.getParameters()
        log.info("finding... ")
        for (record in ecocropcsv) {
            var flag = false
            for (col in par.keys) {
                val `val` = par[col]
                if (`val` == null) continue
                log.info("checking {}", record.get(col))
                if (record.get(col).contains(`val`)) {
                    flag = true
                }
            }
            if (flag) return record
        }
        return null
    }

    @JvmStatic
    fun getRecord(query: String, column: String?): CSVRecord? {
        log.info("finding {} in {}", query, column)
        for (i in ecocropcsv) {
//            log.info("checking {}", i.get(column));
            if (i.get(column).contains(query)) {
                return i
            }
        }
        log.info("{} not found in {}", query, column)
        return null
    }

    private val science_perawatancsv: MutableMap<String?, CSVRecord?> = HashMap<String?, CSVRecord?>()
    fun perawatanCsv(ecocropcsv: CSVRecord): CSVRecord? {
        if (science_perawatancsv.containsKey(ecocropcsv.get(E.Science_name)))
            return science_perawatancsv[ecocropcsv[E.Science_name]]
        for (i in perawatancsv) {
            if (i.get(E.Science_name).contains(ecocropcsv.get(E.Science_name))) {
                science_perawatancsv.put(ecocropcsv.get(E.Science_name), i)
                return i
            }
        }
        return null
    }
}
