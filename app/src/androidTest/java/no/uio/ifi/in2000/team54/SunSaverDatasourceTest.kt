package no.uio.ifi.in2000.team54

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team54.data.shared.SunSaverDatasource
import no.uio.ifi.in2000.team54.database.RoofSectionEntity
import no.uio.ifi.in2000.team54.database.SolarArrayEntity
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
    private lateinit var solarArrayDao: SunSaverDao
    private lateinit var solarArrayDatabase: SunSaverDatabase
    private lateinit var solarArrayDatasource: SunSaverDatasource

    private val solarArrayEntity = SolarArrayEntity(
        name = "test1",
        panelType = "Premium",
        latitude = "10.100", // should be transformed by repository
        longtitude = "59.1589"
    )

    private val roofSectionEntity1 = RoofSectionEntity(
        // id will be generated automatically
        solarPanelName = "", // datasource has to set it
        area = 23.0,
        incline = 35.0,
        direction = 120.0,
        panels = 9,
        mapId = "1",
    )

    private val roofSectionEntity2 = RoofSectionEntity(
        // id will be generated automatically
        solarPanelName = "", // datasource has to set it
        area = 14.0,
        incline = 35.0,
        direction = 210.0,
        panels = 2,
        mapId = "2",
    )

    private val solarArrayWithRoofSections = SolarArrayWithRoofSections(
        solarArray = solarArrayEntity,
        roofSections = listOf(roofSectionEntity1, roofSectionEntity2)

    )

    @Before // do this before each test
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()

        solarArrayDatabase = Room.inMemoryDatabaseBuilder(context, SunSaverDatabase::class.java)
            .allowMainThreadQueries() // just for testing
            .build()
        solarArrayDao = solarArrayDatabase.sunSaverDao()
        solarArrayDatasource = SunSaverDatasource(solarArrayDao)
    }

    @After // do this after each test
    @Throws(IOException::class)
    fun closeDatabase() {
        solarArrayDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetASolarPanel() = runBlocking {
        solarArrayDatasource.insertSolarArrayWithRoofSections(solarArrayWithRoofSections)
        val solarArray: SolarArrayWithRoofSections = solarArrayDatasource.getAllSolarArrays()[0]

        assertEquals(solarArray.solarArray, solarArrayEntity)

        val roofSections = solarArray.roofSections.sortedBy { it.mapId }
        assertEquals(roofSections.size, 2)
        assertEquals(roofSections[0].solarPanelName, solarArray.solarArray.name)
        // assertEquals(roofSections[0].direction, 120.0)
        assertEquals(roofSections[1].panels, 2)
    }
}