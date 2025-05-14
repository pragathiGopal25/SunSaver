package no.uio.ifi.in2000.team54.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Room DAOs provide methods such as update, insert, and delete data in the database.
@Dao
interface SunSaverDao {

    // returns created id
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSolarArray(solarArray: SolarArrayEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRoofSections(roofSections: List<RoofSectionEntity>)

    // needs "Transaction" since it runs two queries,
    // one for getting solar arrays and the second one for roof sections
    @Transaction
    @Query("SELECT * FROM SolarArrays")
    fun getAllSolarArrays(): Flow<List<SolarArrayWithRoofSections>>

    // deleting solarArray will delete all roof sections because of ForeignKey.CASCADE in Entity
    // definition
    @Delete
    suspend fun delete(solarArray: SolarArrayEntity)

    @Update
    suspend fun updateSolarArray(solarArray: SolarArrayEntity)

    @Update // returns number of rows updated
    suspend fun updateRoofSections(roofSections: List<RoofSectionEntity>): Int

    // helper methods for updating in datasource
    @Delete
    suspend fun deleteRoofSections(roofSections: List<RoofSectionEntity>)

    @Query("SELECT * FROM RoofSections WHERE solarArrayId = :id")
    suspend fun getRoofSectionsBySolarArrayId(id: Long): List<RoofSectionEntity>
}