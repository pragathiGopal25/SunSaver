package no.uio.ifi.in2000.team54.data.shared

import no.uio.ifi.in2000.team54.database.SolarArrayWithRoofSections
import no.uio.ifi.in2000.team54.database.SunSaverDao
import javax.inject.Inject

class SunSaverDatasource @Inject constructor(
    private val sunSaverDao: SunSaverDao
){
    suspend fun insertSolarArrayWithRoofSections(solarArrayWithRoofSections: SolarArrayWithRoofSections): Long {
        val id: Long = sunSaverDao.insertSolarArray(solarArrayWithRoofSections.solarArray)

        // add foreign key from solar array
        val roofSectionsWithForeignKey = solarArrayWithRoofSections.roofSections.map {
            it.copy(solarArrayId = id)
        }
        sunSaverDao.insertRoofSections(roofSectionsWithForeignKey)
        return id
    }

    suspend fun getAllSolarArrays(): List<SolarArrayWithRoofSections> {
        return sunSaverDao.getAllSolarArrays()
    }

    suspend fun delete(solarArrayWithRoofSections: SolarArrayWithRoofSections) {
        sunSaverDao.delete(solarArrayWithRoofSections.solarArray)
    }
}