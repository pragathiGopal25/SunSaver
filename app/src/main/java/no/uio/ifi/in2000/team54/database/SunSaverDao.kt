package no.uio.ifi.in2000.team54.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

// Room DAOs provide methods such as update, insert, and delete data in the database.
@Dao
interface SunSaverDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) // todo: check if should be changed
    suspend fun insertSolarArray(solarArray: SolarArrayEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRoofSections(roofSections: List<RoofSectionEntity>)

    // needs "Transaction" since it runs two queries,
    // one for getting solar arrays and the second one for roof sections
    @Transaction
    @Query("SELECT * FROM SolarArrays")
    suspend fun getAllSolarArrays(): List<SolarArrayWithRoofSections>

    // deleting solarArray will delete all roof sections because of ForeignKey.CASCADE in Entity
    // definition
    @Delete
    suspend fun delete(solarArray: SolarArrayEntity)
}