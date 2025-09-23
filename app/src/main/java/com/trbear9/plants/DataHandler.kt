@file:Suppress("DEPRECATION")

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
            resource = ClassPathResource("EcoCrop_DB.csv")
            resource.inputStream.use { `is` ->
                InputStreamReader(`is`).use {
                        reader -> ecocropcsv = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).records
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    fun isAuthored(record: CSVRecord): Boolean {
        return record.get(E.Authority) != null || !record.get(E.Authority).isEmpty()
    }

    @JvmStatic
    fun getScienceName(record: CSVRecord): String {
        return record.get(E.Science_name)
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
    fun process(userVariable: UserVariable): MutableMap<Int, MutableSet<CSVRecord>> {
        val map: MutableMap<Int, MutableSet<CSVRecord>> =
            TreeMap<Int, MutableSet<CSVRecord>>(Comparator.reverseOrder())
        val parameters = userVariable.parameters.values

        for (record in process()) {
            explored_fields++
            var score = 0
            var flag = false
            for (parameter in parameters) {
                val params = parameter.getParameters()
                for (col in params.keys) {
                    val paramVal = params[col]
                    paramVal?:continue
                    var floatVar = Float.Companion.MAX_VALUE
                    try { floatVar = paramVal.toFloat() }
                    catch (_: NumberFormatException) { }

                    when (col) {
                        "LAT" -> {
                            val bias = 10
                            if (record.get(E.O_minimum_latitude) == "NA" ||
                                record.get(E.O_maximum_latitude) == "NA"
                            ) continue

                            val min = record.get(E.O_minimum_latitude).toFloat()
                            val max = record.get(E.O_maximum_latitude).toFloat()
                            if ((min <= floatVar && max >= floatVar)) {
                                score += 1
                                flag = true
                            } else score += (if (floatVar < min) floatVar - min - bias else max - floatVar - bias).toInt()

                            if (record.get(E.A_minimum_latitude) == "NA" ||
                                record.get(E.A_maximum_latitude) == "NA"
                            ) continue

                            val amin = record.get(E.A_minimum_latitude).toFloat()
                            val amax = record.get(E.A_maximum_latitude).toFloat()
                            if (amin <= floatVar && amax >= floatVar) {
                                score += 1
                                flag = true
                            }
                        }

                        "ALT" -> {
                            if (record.get(E.O_maximum_altitude) == "NA") continue
                            val altitude = record.get(E.O_maximum_altitude).toFloat()
                            if (altitude >= floatVar) {
                                score += 1
                                flag = true
                            } else score -= abs(altitude - floatVar).toInt()
                        }

                        "RAIN" -> {
                            if (record.get(E.O_minimum_rainfall) == "NA" || record.get(E.O_maximum_rainfall) == "NA") continue
                            val min = record.get(E.O_minimum_rainfall).toFloat()
                            val max = record.get(E.O_maximum_rainfall).toFloat()
                            if (min <= floatVar && max >= floatVar) {
                                score += 1
                                flag = true
                            } else {
                                val floatVar = floatVar/25 // bias
                                score +=  // -1 score/25 rainfall
                                    (if (floatVar < min) floatVar - min else max - floatVar).toInt()
                            }
                        }

                        "TEMPMAX" -> {
                            if (record.get(E.O_maximum_temperature) == "NA") continue
                            val max = record.get(E.O_maximum_temperature).toFloat()
                            if (max >= floatVar) {
                                score += 1
                                flag = true
                            } else score -= abs(max - floatVar).toInt()
                        }

                        "TEMPMIN" -> {
                            if (record.get(E.O_minimum_temperature) == "NA") continue
                            val min = record.get(E.O_minimum_temperature).toFloat()
                            if (min <= floatVar) {
                                score += 1
                                flag = true
                            } else score -= abs(floatVar - min).toInt()
                        }

                        "PANEN" -> {
                            if (record.get(E.MIN_crop_cycle) == "NA" || record.get(E.MAX_crop_cycle) == "NA") continue
                            val min = record.get(E.MIN_crop_cycle).toFloat()
                            val max = record.get(E.MAX_crop_cycle).toFloat()
                            if (min <= floatVar && max >= floatVar) {
                                ++score
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
                            if (record.get(E.O_minimum_ph) == "NA" || record.get(E.A_minimum_ph) == "NA") continue
                            val min = record.get(E.O_minimum_ph).toFloat()
                            val max = record.get(E.O_maximum_ph).toFloat()
                            if (min <= floatVar && max >= floatVar) {
                                score += 1
                                flag = true
                            } else score +=  // minus n per ph yang diluar jangkauan
                                (if (floatVar < min) floatVar - min else max - floatVar).toInt()
                        }

                        else -> {
                            val value = record.get(col)
                            if ((col == E.O_soil_texture || col == E.A_soil_texture) &&
                                record.get(col) == "wide"
                            ) {
                                score += 2
                                flag = true
                                continue
                            }

                            if (value.contains(paramVal)) {
                                score += if (col == E.Climate_zone) 3
                                else 2
                                flag = true
                            } else if (col == E.Climate_zone) {
                                score -= 354
                                flag = false
                            } else if (col == E.O_soil_drainage) {
                                val split: Array<String?> =
                                    value.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                if (split.size > 1) continue
                                val drainage = when (value) {
                                    "well (dry spells)" -> DRAINAGE.well
                                    "poorly (saturated >50% of year)" -> DRAINAGE.poorly
                                    "excessive (dry/moderately dry)" -> DRAINAGE.excessive
                                    else -> null
                                }
                                if (drainage == null) continue
                                if (split.size == 1 && drainage.ordinal - DRAINAGE.valueOf(paramVal).ordinal >= 2) {
                                    score -= 7
                                    flag = false
                                }
                            }
                        }
                    }
                }
            }
            if (isAuthored(record) && flag && score > 0) map.computeIfAbsent(score) { k: Int? -> HashSet<CSVRecord>() }
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
        for (record in process()) {
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
        for (i in process()) {
//            log.info("checking {}", i.get(column));
            if (i.get(column).contains(query)) {
                return i
            }
        }
        log.info("{} not found in {}", query, column)
        return null
    }

    fun process(): MutableList<CSVRecord> {
        return ecocropcsv
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
