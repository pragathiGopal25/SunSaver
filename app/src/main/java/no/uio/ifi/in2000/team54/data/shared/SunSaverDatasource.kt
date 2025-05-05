package no.uio.ifi.in2000.team54.data.shared

import kotlinx.coroutines.flow.Flow
import no.uio.ifi.in2000.team54.database.SolarArrayWithRoofSections
import no.uio.ifi.in2000.team54.database.SunSaverDao
import javax.inject.Inject

interface ISunSaverDatasource { // define interface (also used for testing)
    suspend fun insert(solarArrayWithRoofSections: SolarArrayWithRoofSections): Long
    fun getAllSolarArrays(): Flow<List<SolarArrayWithRoofSections>>
    suspend fun delete(solarArrayWithRoofSections: SolarArrayWithRoofSections)
    suspend fun update(solarArrayWithRoofSections: SolarArrayWithRoofSections)
}

class SunSaverDatasource @Inject constructor(
    private val sunSaverDao: SunSaverDao
): ISunSaverDatasource {
    override suspend fun insert(solarArrayWithRoofSections: SolarArrayWithRoofSections): Long {
        val id: Long = sunSaverDao.insertSolarArray(solarArrayWithRoofSections.solarArray)

        // add foreign key from solar array
        val roofSectionsWithForeignKey = solarArrayWithRoofSections.roofSections.map {
            it.copy(solarArrayId = id)
        }
        sunSaverDao.insertRoofSections(roofSectionsWithForeignKey)
        return id
    }

    override fun getAllSolarArrays(): Flow<List<SolarArrayWithRoofSections>> {
        return sunSaverDao.getAllSolarArrays()
    }

    override suspend fun delete(solarArrayWithRoofSections: SolarArrayWithRoofSections) {
        sunSaverDao.delete(solarArrayWithRoofSections.solarArray)
    }

    // we have two edge cases: added and deleted solar arrays
    // the problem is that update does nothing if it doesn't find a matching key
    // therefore we have to handle this manually in this method
    override suspend fun update(solarArrayWithRoofSections: SolarArrayWithRoofSections) {

        // updating the solar array first
        sunSaverDao.updateSolarArray(solarArrayWithRoofSections.solarArray)

        // foreign key connection is lost during getting, so need to assign it again
        val id: Long = solarArrayWithRoofSections.solarArray.id
        // add foreign key from solar array
        val roofSectionsWithForeignKey = solarArrayWithRoofSections.roofSections.map {
            it.copy(solarArrayId = id)
        }

        // updating rows
        sunSaverDao.updateRoofSections(roofSectionsWithForeignKey)

        // handling edge cases
        // case 1: we want to delete those that aren't updated
        // get the saved ids
        val savedRoofSections = sunSaverDao.getRoofSectionsBySolarArrayId(id)

        // we want to check which ids are missing - those have to be deleted
        val idOfUpdated = roofSectionsWithForeignKey.map {
            it.roofSectionId
        }.toSet()

        // want to find the savedRoofSections that aren't in this list
        val deletedRoofSections = savedRoofSections.filter { it.roofSectionId !in idOfUpdated }

        // delete those
        sunSaverDao.deleteRoofSections(deletedRoofSections)

        // case 2: we want to add new roof sections.
        // they have roofSectionId = 0
        val addedRoofSections = roofSectionsWithForeignKey.filter {
            it.roofSectionId == 0L // 0 is a placeholder
        }
        sunSaverDao.insertRoofSections(addedRoofSections)
    }
}