
```mermaid
classDiagram
    %% SunSaver
    class ISunSaverRepository {
        <<Interface>>
        + getAllSolarArrays() Flow~list of SolarArray~
        + addSolarArray(SolarArray)
        + deleteSolarArray(SolarArray)
        + updateSolarArray(SolarArray)
    }
    class SunSaverRepository {
        - datasource: ISunSaverDatasource
        + getAllSolarArrays() Flow~list of SolarArray~
        + addSolarArray(SolarArray)
        + deleteSolarArray(SolarArray)
        + updateSolarArray(SolarArray)
    }
    ISunSaverRepository <|.. SunSaverRepository
    SunSaverRepository -- ISunSaverDatasource: lagrer og henter data
    
    class SunSaverDatasource {
        - sunSaverDao: SunSaverDao 
        + insert(SolarArrayWithRoofSections) Long
        + getAllSolarArrays(): Flow~list of SolarArrayWithRoofSections~
        + delete(SolarArrayWithRoofSections)
        + update(SolarArrayWithRoofSections)
    }
    class ISunSaverDatasource {
        <<Interface>>
        + insert(SolarArrayWithRoofSections) Long
        + getAllSolarArrays(): Flow~list of SolarArrayWithRoofSections~
        + delete(SolarArrayWithRoofSections)
        + update(SolarArrayWithRoofSections)
    }
    ISunSaverDatasource <|.. SunSaverDatasource
    
    %% Database part
    class SunSaverDao {
        <<Interface>>
        + insertSolarArray(SolarArrayEntity) Long
        + insertRoofSections(List~RoofSectionEntity~)
        + getAllSolarArrays() Flow~ list of SolarArrayWithRoofSections~
        + delete(SolarArrayEntity)
        + updateSolarArray(SolarArrayEntity)
        + updateRoofSections(List~RoofSectionEntity~)
        + deleteRoofSections(List~RoofSectionEntity~)
        + getRoofSectionBySolarArrayId(Long) List~RoofSectionEntity~
    }

    SunSaverDatasource -- SunSaverDao
    
    class SunSaverDatabase {
        <<Abstract>>
        + sunSaverDao() SunSaverDao
    }
    class RoomDatabase {
    }
    SunSaverDatabase --|> RoomDatabase

    class SolarArrayEntity {
        + id: Long
        + name: String
        + panelType: String
        + latitude: Double
        + longitude: Double
        + powerConsumption: Double
        + address: String
    }
    class RoofSectionEntity {
        + roofSectionId: Long
        + solarArrayId: Long
        + area: Double
        + incline: Double
        + direction: Double
        + panels: Int
        + mapId: String
    }
    RoofSectionEntity "1..*" --* "1" SolarArrayEntity
    SolarArrayEntity "1" -- "1" SolarArrayWithRoofSections
    
    class SolarArrayWithRoofSections {
        + solarArray: SolarArrayEntity
        + roofSections: List~RoofSectionEntity~
    }

    SolarArrayWithRoofSections -- SunSaverDao
    SolarArrayEntity -- SunSaverDao
    SunSaverDatabase -- SunSaverDao

    SolarArrayEntity -- SunSaverDatabase: tabell
    RoofSectionEntity -- SunSaverDatabase: tabell     
    
    SolarArrayWithRoofSections -- ISunSaverDatasource
```

Assosiasjonen fra RoofSectionEntity til SolarArrayEntity er 1..* til 1, dvs. en RoofSectionEntity er innehold i nøyaktig ett SolarArrayEntity, og en SolarEntity må ha referanse til minst en takflate. På grunn av denne strenge relasjonen, vil vi ikke tegne flere linjer fra RoofSectionEntity til andre klasser, siden ingen klasser bruker kun RoofSectionEntity, og det underforstått at hvis en klasse refererer til SolarArrayEntity, så refererer den til en eller flere RoofSectionEntity. Dette valget er gjort for å forbedre leseligheten av diagrammet.