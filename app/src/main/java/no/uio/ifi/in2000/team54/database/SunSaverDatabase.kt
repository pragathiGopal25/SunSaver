package no.uio.ifi.in2000.team54.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SolarArrayEntity::class, RoofSectionEntity:: class], version = 1, exportSchema = false)
abstract class SunSaverDatabase: RoomDatabase() { // the class is abstract because room implements it for us
    abstract fun sunSaverDao(): SunSaverDao // associates database with dao
}