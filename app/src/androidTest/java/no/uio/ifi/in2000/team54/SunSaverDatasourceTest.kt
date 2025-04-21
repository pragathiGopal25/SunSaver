package no.uio.ifi.in2000.team54

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team54.data.shared.SunSaverDatasource
import no.uio.ifi.in2000.team54.database.SolarArrayWithRoofSections
import no.uio.ifi.in2000.team54.database.SunSaverDao
import no.uio.ifi.in2000.team54.database.SunSaverDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SolarArrayDatasourceTest {
    private lateinit var sunSaverDao: SunSaverDao
    private lateinit var sunSaverDatabase: SunSaverDatabase
    private lateinit var sunSaverDatasource: SunSaverDatasource

    @Before // do this before each test
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()

        sunSaverDatabase = Room.inMemoryDatabaseBuilder(context, SunSaverDatabase::class.java)
            .allowMainThreadQueries() // just for testing
            .build()
        sunSaverDao = sunSaverDatabase.sunSaverDao()
        sunSaverDatasource = SunSaverDatasource(sunSaverDao)
    }

    @After // do this after each test
    @Throws(IOException::class)
    fun closeDatabase() {
        sunSaverDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetASolarPanel() = runBlocking {
        sunSaverDatasource.insertSolarArrayWithRoofSections(FakeTestData.solarArrayWithRoofSections)
        val solarArray: SolarArrayWithRoofSections = sunSaverDatasource.getAllSolarArrays()[0]

        assertEquals(solarArray.solarArray, FakeTestData.solarArrayA)

        val roofSections = solarArray.roofSections.sortedBy { it.mapId }
        assertEquals(roofSections.size, 2)
        assertEquals(roofSections[0].solarPanelName, solarArray.solarArray.name)
        assertEquals(roofSections[0].direction, 120.0, 0.0)
        assertEquals(roofSections[1].panels, 2)
    }
}