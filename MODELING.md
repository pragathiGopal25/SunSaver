# TODO 
- Aktivitetsdiagram skal inneholde hvordan interaksjonen ser ut fra brukerens perspektiv. Den skal f.eks. inkludere validering av brukerinput, som ikke blir inkludert i sekvensdiagrammet for å gjøre sekvensdiagrammet 
mer arkitekturnært, mens aktivitetsdiagrammet skal være mer brukerinteraksjonsnært. Aktivitetsdiagrammet for use case "lagre ny" bør herved inneholde (i tillegg til resten av brukerinteraksjon i sekvensdiagrammet): 
validering av om brukeren har fylt ut alle felt; hendelsesforløp der bruker ønsker å endre (f.eks.) antall paneler i et valgt takflate; bruker bytter type på solcellepaneller


# Modellering
### Inkluderte diagrammer: 
- Use case diagram: Gir en generell oversikt over de viktigste funksjonene appen tilbyr brukeren. 
- Klassediagram: Viser arkiteturen i prosjektet og hvilke dataklasser som er brukt. 
- Sekvensdiagrammer: for utvalgte/hver use case viser hvordan de ulike komponentene (fra klassediagrammet) kommuniserer sammen for å utføre use caset. Den fokuserer primært på appens komponenter, og overlater brukerinteraksjonen til aktivitetsdiagrammet. 
- Aktivitetsdiagrammet: for utvalgte/hver use case viser hvordan brukeren skal interagere med appen. 


## Use case diagram 
![Use case diagram for SunSaver](image.png)
Diagrammet ble laget ved hjelp av [app.diagrams.net](https://app.diagrams.net/) siden Mermaid ikke har Use case diagrammer. <br>

## Klassediagram
Formålet med klassediagrammet er å vise strukturen i prosjektet vårt.
```mermaid
classDiagram
    class SolarArray {
        + id: Long
        + name: String
        + panelType: SolarPanelType
        + roofSections: RoofSection
        + coordinates: Coordinates
        + powerConsumption: Double
        + address: String
    }

    class RoofSection {
        + id: Long?
        + area: Double
        + incline: Double
        + direction: Double
        + panels: Int
        + mapId: String?
    }
    RoofSection "1..*" --* "1" SolarArray : roofsections
    SolarArray -- HomeViewModel
    SolarArray -- ISunSaverRepository
    SolarArray -- ManageSolarArrayViewModel
 
    %% HomeViewModel
    class NetworkObserver {
        + isConnected: Flow~Boolean~
        - connectivityManager: ConnectivityManager
        + isNetworkAvailable() Boolean
    }
        class Elements {
        <<Enumeration>>
        +TEMP
        + CLOUD
        + SNOW
        + IRRIDANCE
        + SUNHOURS
    }

    class HomeUiState {
        + solarArrays: List~SolarArray~ 
        + selectedSolarArray: SolarArray? 
        + priceData: PriceData 
        + electricityProductionData: Map~String, list of Double~
        + timeScope: TimeScope
        + timeUntilRecoup: Double 
    }

    class LoadingState {
        + isLoading: Boolean 
        + statusMessage: String
    }

    class TimeScope {
        <<Enumeration>>
            + DAY 
            + MONTH
            + YEAR
    }
    
    NetworkObserver -- HomeViewModel
    TimeScope -- HomeUiState
    HomeUiState -- HomeViewModel
    TimeScope -- HomeViewModel
    LoadingState -- HomeViewModel
    Elements -- HomeViewModel

 
    %% LOTS of data classes??? TODO
    class HomeViewModel {
        - networkObserver: NetworkObserver
        - _repository: FrostRepository
        - _sunSaverRepository: SunSaverRepository
        - electricityPriceRepository: ElectricityPriceRepository

        - _priceLoadingState: MutableStateFlow~LoadingState~
        + priceLoadingState: StateFlow~LoadingState~

        - _graphLoadingState: MutableStateFlow~LoadingState~
        + graphLoadingState: StateFlow~LoadingState~

        - _homeUiState: MutableStateFlow~HomeUiState~
        + homeUiState: StateFlow~HomeUiState~

        - _snackbarMessage: MutableSharedFlow~String~
        + snackbarMessage: SharedFlow~String~

        - _isOnline: MutableStateFlow~Boolean~
        + isOnline: StateFlow~Boolean~

        - electricityProductionMap: MutableMap~SolarArray, list of Double~
        - electricityPriceMap: MutableMap~SolarArray, MutableMap of TimeScope and PriceData~
        - weatherDataMap: MutableMap~SolarArray, WeatherData~
        - priceDataMap: MutableMap~SolarArray, MutableMap of TimeScope and Double~
        - timeScopeToDays: Map~TimeScope, Int~

        + selectSolarArray(SolarArray, Boolean?)
        - observeNetwork()
        - getWeatherData(SolarArray)
        - useWeatherData(SolarArray)
        - getPriceData(SolarArray)
        + removeSolarArray(SolarArray)
        - loadElectricityPrices(SolarArray)
        - seePrices(TimeScope, SolarArray)
        + changeTimeScope(TimeScope)
        - calculateRecoup(SolarArray)
        - findUpdated(List~SolarArray~, List~SolarArray~) SolarArray?
    }
    
    %% Frost: Classes 
    class FrostRepository {
        - datasource: FrostDatasource
        + getData(Coordinates, Elements) Map~String, Double~
        - getMonthlyAverageValues(List~ObservationData~) Map~String, Double~
    }

    class FrostDatasource {
        - client: HttpClient
        - raw: String
        - encoded: String
        - authHeader: String
        - nameMap: Map~Elements, String~
        - sensorMap: MutableMap~Elements, mutable list of String~
        - referenceTome: String
        - fetchNearestSource(Coordinates, Elements) MutableMap~Elements, list of String~
        + fetchObservationDataFromFrost(Coordinates, Elements) List~ObservationData~
    } 

    class ObservationData {
        + sourceId: String
        + referenceTime: String
        + observations: List~Observation~
    }

    class Observation {
        + elementId: String
        + value: Double
        + unit: String
        + qualityCode: Double
    }
    
    ObservationData -- Observation
    FrostRepository --> FrostDatasource: fetcher værdata (snø, skydekke, temperatur, flux, soltimer)
    HomeViewModel --> FrostRepository: fetcher gjennomsnittlige verdier av værdata

    Elements -- FrostRepository
   
    
    ObservationData -- FrostRepository
    ObservationData -- FrostDatasource 
    Elements -- FrostDatasource
    %% ElectricityPrice
    class ElectricityPriceRepository {
        - datasource: ElectricityPriceDataSource
        + getPriceData(Int, Double, Double, Double) List~Double~
        + getMonth() Int
        + getPriceDataInterval(Int, String) List~Double~
        - decrementDate(String) String
        + getPriceAreal(Coordinates) String

    }
    class ElectricityPriceDataSource {
        - client: HttpClient
        + getElectricityPrices(String, String): List~ElectricityPriceInfo~
    }
    HomeViewModel --> ElectricityPriceRepository: gjennomsnittlig strømpris gitt tidsintervall
    ElectricityPriceRepository --> ElectricityPriceDataSource: fetcher NOK per kWn per dag
    

    %% ViewModel TODO: Data classes here too
    class ManageSolarArrayViewModel {
        - repository: BuildingRepository
        - _sunSaverRepository: SunSaverRepository

        - _currentSolarArray: MutableStateFlow~SolarArray~
        + currentSolarArray: StateFlow~SolarArray~

        - _mapAddress: MutableStateFlow~AddressState~
        + mapAddress: StateFlow~AddressState~

        - _mapSearchAddress: MutableStateFlow~SearchAddressState~
        + mapAddress: StateFlow~SearchAddressState~
        
        + mapRoofSections: StateFlow~MapRoofSectionsState~
        + mapSearchAddressSuggestions: StateFlow~AddressSuggestionsState~
        
        + setMapAddress(Address)
        + setSearchAddress(String)
        + updateSolarArrayAddress(SolarArray?)
        + addSolarArray(SolarArray)
        + queryAddressAtPos(Pos)
        + updateSolarArray(SolarArray)
        + getSolarArray(Long)
        + resetUpdSolarArray()
    }

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
    class SolarArrayWithRoofSections {
        + solarArray: SolarArrayEntity
        + roofSections: List~RoofSectionEntity~
    }

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

    
    class SunSaverDatabase {
        <<Abstract>>
        + sunSaverDao() SunSaverDao
    }
    class RoomDatabase {
    }

 
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
    
    ISunSaverRepository <|.. SunSaverRepository
    SunSaverRepository -- ISunSaverDatasource: lagrer og henter data
    
    ISunSaverDatasource <|.. SunSaverDatasource
    ISunSaverDatasource -- SolarArrayWithRoofSections
    SunSaverDatasource -- SunSaverDao
    SunSaverDatabase --|> RoomDatabase
    SunSaverDao -- SunSaverDatabase
    SolarArrayWithRoofSections -- SunSaverDao
    SunSaverDao -- SolarArrayEntity
    
    SunSaverDatabase -- RoofSectionEntity: tabell     
    SunSaverDatabase -- SolarArrayEntity: tabell

    
    SolarArrayEntity "1" *-- "1..*" RoofSectionEntity
    SolarArrayWithRoofSections "1" -- "1" SolarArrayEntity
    
    
    ManageSolarArrayViewModel -- ISunSaverRepository: lagrer og henter data
    HomeViewModel -- ISunSaverRepository: lagrer og henter data
    namespace Database {
        class SolarArrayEntity
        class RoofSectionEntity 
        class RoomDatabase
        class SunSaverDatabase
        class SunSaverDao
    }
    %% Building 
    class BuildingRepository {
        - dataSource: BuildingDataSource
        + getAddressSuggestions(String) List~Address~
        + getNearestAddressToPos(Pos) Address? 
        + getRoofSections(Address) List~MapRoofSections~
        - getBuildingIds(Address) List~String~
    }

    class BuildingDataSource {
        - client: HttpClient
        + getAddressSuggestions(String) List~Address~
        + getAddressFromPos(Pos) List~Address~
        + getCadastreId(Address) Long? 
        + getBuildingIds(Long) List~String~
        + getRoofSections(String) List~MapRoofSections~
    }
    BuildingRepository --> BuildingDataSource: fetcher data om takflater, koordinater og adresser
    ManageSolarArrayViewModel --> BuildingRepository: fetcher adressedata med bygningsdata

```

Kommentarer: 
- Siden Mermaid og markdown ikke støttet to <> inni hverandre, har jeg brukt "of" i disse tilfellene. For eksempel Flow&lt;list of SolarArray&gt;. 
- HomeViewModel ble veldig stor. Det er fordi den håndterer mye data, og har StateFlows (som i god praksis krever en privat mutable versjon og offentlig immutable)
- Om databasen: Vi lager en abstrakt klasse SunSaverDatabase som arver fra RoomDatabase, og Room-biblioteket fikser implementasjonen for oss. Vi inkluderte RoomDatabase for å vise arv, men den er tom siden den kommer fra Room-biblioteket. 
- SolarArray og SunSaverRepository: Siden det allerede er en assosiasjon mellom SolarArray og ISunSaverRepository, og SunSaverRepository implementerer dette interfacet, lager vi ikke en egen assosiasjon mellom SolarArray og SunSaverRepository, da dette er underforstått gjennom arv. Det samme gjelder for SolarArrayWithRoofSections og SunSaverDatasource.
