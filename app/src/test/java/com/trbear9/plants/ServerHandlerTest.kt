package com.trbear9.plants

import com.openmeteo.api.Forecast
import com.openmeteo.api.Forecast.Daily.sunrise
import com.openmeteo.api.Forecast.Daily.temperature2mMax
import com.openmeteo.api.OpenMeteo
import com.openmeteo.api.common.Elevation
import com.openmeteo.api.common.Response
import com.openmeteo.api.common.time.Date
import com.openmeteo.api.common.units.PrecipitationUnit
import com.openmeteo.api.common.units.TemperatureUnit
import com.trbear9.plants.api.GeoParameters
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.run

class ServerHandlerTest {
    val log = LoggerFactory.getLogger(ServerHandlerTest::class.java)
    @OptIn(Response.ExperimentalGluedUnitTimeStepValues::class)
    @Test
    fun weather(){
        var max = 0.0
        var min = 0.0
        var elevation = 0f
        val meteo = OpenMeteo(-7.2565293f, 110.402824f)
        val response = meteo.forecast(){
            latitude = -7.2565293f
            longitude = 110.402824f
            temperatureUnit = TemperatureUnit.Celsius
            elevation
            startDate = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 60)
            endDate = Date(System.currentTimeMillis())
            daily = Forecast.Daily{
                listOf(temperature2mMin, temperature2mMax)
            }
        }.getOrThrow()
        Forecast.Daily.run {
            response.daily.getValue(temperature2mMax).run {
                log.info("# $temperature2mMax ($unit)")
                for (m in values.values)
                    max+= m?:28.0
                max/= values.size
            }
            response.daily.getValue(temperature2mMin).run {
                log.info("# $temperature2mMin ($unit)")
                for (m in values.values)
                    min+= m?:20.0
                min/= values.size
            }
        }
        val el = meteo.elevation {
            latitude = (-7.257281798437764).toString()
            longitude = 110.4031409940034.toString()
        }.getOrThrow()
        for (f in el.elevation) {
            elevation = f
        }
        elevation/=el.elevation.size
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}