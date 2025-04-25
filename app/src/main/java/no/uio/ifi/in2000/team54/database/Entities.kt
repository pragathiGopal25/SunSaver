package no.uio.ifi.in2000.team54.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

// Entities represent tables
@Entity(tableName = "SolarArrays")
data class SolarArrayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val panelType: String,
    val latitude: Double, // repository will have to handle this mapping
    val longitude: Double,
    val powerConsumption: Double,
)

@Entity(tableName = "RoofSections",
    foreignKeys = [ // establishing "connection" between SolarArrayEntity and RoofSectionEntity
        ForeignKey(
            entity = SolarArrayEntity::class,
            parentColumns = ["id"],
            childColumns = ["solarArrayId"],
            onDelete = ForeignKey.CASCADE // if the foreign key is deleted from solar_arrays, all those rows referenced by it will also be deleted
        )
    ]
)
data class RoofSectionEntity (
    @PrimaryKey(autoGenerate = true)
    val roofSectionId: Long = 0,
    val solarArrayId: Long,
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
        parentColumn = "id",
        entityColumn = "solarArrayId"
    )
    val roofSections: List<RoofSectionEntity>
)