package no.uio.ifi.in2000.team54.data.shared

import no.uio.ifi.in2000.team54.database.SolarArrayWithRoofSections
import no.uio.ifi.in2000.team54.database.SunSaverDao
import javax.inject.Inject

class SunSaverDatasource @Inject constructor(
    private val sunSaverDao: SunSaverDao
){
    suspend fun insertSolarArrayWithRoofSections(solarArrayWithRoofSections: SolarArrayWithRoofSections) {
        sunSaverDao.insertSolarArray(solarArrayWithRoofSections.solarArray)

        val roofSectionsWithForeignKey = solarArrayWithRoofSections.roofSections.map {// add foreign key from solar array
            it.copy(
                solarPanelName = solarArrayWithRoofSections.solarArray.name
            )
        }
        sunSaverDao.insertRoofSections(roofSectionsWithForeignKey)
    }

    suspend fun getAllSolarArrays(): List<SolarArrayWithRoofSections> {
        return sunSaverDao.getAllSolarArrays()
    }
}