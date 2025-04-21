package no.uio.ifi.in2000.team54.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

// Entities represent tables
@Entity(tableName = "SolarArrays")
data class SolarArrayEntity(
    @PrimaryKey
    val name: String,
    val panelType: String,
    val latitude: String, // repository will have to handle this mapping
    val longtitude: String,
)

@Entity(tableName = "RoofSections",
    foreignKeys = [ // establishing "connection" between SolarArrayEntity and RoofSectionEntity
        ForeignKey(
            entity = SolarArrayEntity::class,
            parentColumns = ["name"],
            childColumns = ["solarPanelName"],
            onDelete = ForeignKey.CASCADE // if the foreign key is deleted from solar_arrays, all those rows referenced by it will also be deleted
        )
    ]
)
data class RoofSectionEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val solarPanelName: String,
    val area: Double,
    val incline: Double,
    val direction: Double,
    val panels: Int,
    val mapId: String,
)

data class SolarArrayWithRoofSections( // just for mapping, not a table (thus no "Entity")
    @Embedded
    val solarArray: SolarArrayEntity,
    @Relation(
        parentColumn = "name",
        entityColumn = "solarPanelName"
    )
    val roofSections: List<RoofSectionEntity>
)