package com.trbear9.plants

import com.trbear9.plants.DataHandler.getScienceName
import com.trbear9.plants.DataHandler.process
import com.trbear9.plants.E.CLIMATE
import com.trbear9.plants.api.*
import org.apache.commons.csv.CSVRecord
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class DataHandlerTest {
    @Test
    fun getRecordWithParameters() {
        var iklim = CLIMATE.tropical_wet
        val par1: Parameters = CustomParameters().apply {
            category = (E.CATEGORY.cereals_pseudocereals)
            lifeSpan = (E.LIFESPAM.annual)
            panen = (90)
            query = ("Oryza")
        }
        val par2: Parameters? = SoilParameters().apply {
            drainage= (E.DRAINAGE.poorly)
            fertility =(E.FERTILITY.high)
        }
        val par3: Parameters? = GeoParameters().apply {
            altitude = (2000).toDouble()
            iklim = (iklim)
            rainfall = (1700).toDouble()
        }
        val userVariable = UserVariable()
        userVariable.add(par1, par2!!, par3!!)

        DataHandler.explored_fields = 0
        val records: MutableMap<Int, MutableSet<CSVRecord>> = process(userVariable)
        log.info("Explored fields: {}", DataHandler.explored_fields)
        log.info("Best score with descending order")
        for (score in records.keys) {
            for (bestValue in records[score]!!) log.info(
                "Best Value: {} with score of {}",
                getScienceName(bestValue),
                score
            )
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger("DATASET TEST")
    }
}