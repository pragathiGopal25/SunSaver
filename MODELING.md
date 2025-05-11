# Modellering
### Inkluderte diagrammer: 
- Use case diagram: Gir en generell oversikt over de viktigste funksjonene appen tilbyr brukeren. 
- Klassediagram: Viser appens struktur og klasser, og hvordan de er relatert til hverandre. 
- Sekvensdiagrammer: for utvalgte/hver use case viser hvordan de ulike komponentene (fra klassediagrammet) kommuniserer for å gjennomføre use caset. Den fokuserer primært på appens komponenter, og overlater brukerinteraksjonen til aktivitetsdiagrammet. 
- Aktivitetsdiagrammet: for utvalgte/hver use case viser mulige scenarioer til hvordan bruker kan interagere med appen. Vi lager aktivitetsdiagrammer kun for de use casene der brukeren må foreta noen valg, f.eks. "lagre et anlegg" og "redigere et anlegg". 


## Use case diagram 
Formålet med appen er at bruker skal kunne legge til en eller flere solcelleanlegg, og administrere dem (altså slette og redigere). Appen har også en infoskjerm, men den er ikke en del av hovedfunksjonaliteten i appen, og dermed er den ikke inkludert i use case diagrammet. <br/>

![Use case diagram for SunSaver](image.png)<br/>

\* estimat om hvor mye man sparer ved å installere dette solcelleanlegget, tid til man har tjent inn det man innvesterte inn i anlegget, og en graf som viser hvordan er strømproduksjonen i området mtp værforhold. <br/>
Redigering, sletting og valg av nytt anlegg er markert med <<extend>> fordi de krever minst et lagret anlegg. <br/>
<br/>
Diagrammet ble laget ved hjelp av [app.diagrams.net](https://app.diagrams.net/) siden Mermaid ikke har Use case diagrammer. <br/>

## Klassediagram
Klassediagrammet fokuserer på arkitekturen i appen vår (ViewModel - Repository - Datasource) og noen av de viktigste dataklasser. Vi inkluderer ikke composables siden de er strengt tatt funksjoner. 

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
    
    namespace Database {
        class SolarArrayEntity
        class RoofSectionEntity 
        class RoomDatabase
        class SunSaverDatabase
        class SunSaverDao
    }

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
    %% relations 

    RoofSection "1..*" --* "1" SolarArray : roofsections
    SolarArray -- HomeViewModel
    SolarArray -- ManageSolarArrayViewModel
    SolarArray -- ISunSaverRepository

    NetworkObserver -- HomeViewModel
    TimeScope -- HomeUiState
    HomeUiState -- HomeViewModel
    TimeScope -- HomeViewModel
    LoadingState -- HomeViewModel
    Elements -- HomeViewModel

    %% Frost 
    ObservationData -- Observation
    FrostRepository --> FrostDatasource: fetcher værdata (snø, skydekke, temperatur, flux, soltimer)
    HomeViewModel --> FrostRepository: fetcher gjennomsnittlige verdier av værdata
    Elements -- FrostRepository
    ObservationData -- FrostRepository
    ObservationData -- FrostDatasource 
    Elements -- FrostDatasource

    HomeViewModel --> ElectricityPriceRepository: gjennomsnittlig strømpris gitt tidsintervall
    ElectricityPriceRepository --> ElectricityPriceDataSource: fetcher NOK per kWn per dag

    %% db 
    
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

    BuildingRepository --> BuildingDataSource: fetcher data om takflater, koordinater og adresser
    ManageSolarArrayViewModel --> BuildingRepository: fetcher adressedata med bygningsdata

    
```

Kommentarer: 
- Siden Mermaid og markdown ikke støttet to <> inni hverandre, har jeg brukt "of" i disse tilfellene. For eksempel Flow&lt;list of SolarArray&gt;. 
- HomeViewModel ble veldig stor. Det er fordi den håndterer mye data, og har StateFlows (som i god praksis krever en privat mutable versjon og offentlig immutable)
- Om databasen: Vi lager en abstrakt klasse SunSaverDatabase som arver fra RoomDatabase, og Room-biblioteket fikser implementasjonen for oss. Vi inkluderte RoomDatabase for å vise arv, men den er tom siden den kommer fra Room-biblioteket. 
- SolarArray og SunSaverRepository: Siden det allerede er en assosiasjon mellom SolarArray og ISunSaverRepository, og SunSaverRepository implementerer dette interfacet, lager vi ikke en egen assosiasjon mellom SolarArray og SunSaverRepository, da dette er underforstått gjennom arv. Det samme gjelder for SolarArrayWithRoofSections og SunSaverDatasource.
- TODO: Må finne ut hvilke klasser skal inkluderes. 

## Use case: Legg til solcelleanlegg
Sekvensdiagrammet under arbeid. Må også ha en aktivitetdiagram her. <br/>
Aktivitetsdiagram skal inneholde hvordan interaksjonen ser ut fra brukerens perspektiv. Den skal f.eks. inkludere validering av brukerinput, som ikke blir inkludert i sekvensdiagrammet for å gjøre sekvensdiagrammet mer arkitekturnært, mens aktivitetsdiagrammet skal være mer brukerinteraksjonsnært. Aktivitetsdiagrammet for use case "lagre ny" bør herved inneholde (i tillegg til resten av brukerinteraksjon i sekvensdiagrammet): 
validering av om brukeren har fylt ut alle felt; hendelsesforløp der bruker ønsker å endre (f.eks.) antall paneler i et valgt takflate; bruker bytter type på solcellepaneller

```mermaid
sequenceDiagram

    %% deklarerer medvirkende
    actor User
    participant HomeScreen
    participant ManageSolarArrayScreen
    participant ManageSolarArrayViewModel 
    participant BuildingRepository 
    participant BuildingDatasource
    participant GeoNorge
    participant Kartverket 
    participant Fjordkraft
    participant SunSaverRepository
    participant SunSaverDatasource 
    participant Database(DAO)

    User ->> HomeScreen: Clicks on + in the navbar
    HomeScreen ->> ManageSolarArrayScreen: navigates to ManageSolarArrayScreen
    ManageSolarArrayScreen -->> User: Show map and address search feild

    alt User searches for an address
        %% address suggestions 
        User ->> ManageSolarArrayScreen: Types an address
        ManageSolarArrayScreen ->> ManageSolarArrayViewModel: setMapAddress(address)
        ManageSolarArrayViewModel ->> BuildingRepository: getAddressSuggestions(address)
        BuildingRepository ->> BuildingDatasource: getAddressSuggestions(address)
        BuildingDatasource ->> GeoNorge: api call for addresses
        GeoNorge -->> BuildingDatasource: http responce
        BuildingDatasource -->> BuildingRepository: list addresses 
        BuildingRepository -->> ManageSolarArrayViewModel: list of addresses
        ManageSolarArrayViewModel -->> ManageSolarArrayScreen: StateFlow of address <br/> suggestions 
        ManageSolarArrayScreen -->> User: Show address suggestions  

        %% bruker velger adresse
        User ->> ManageSolarArrayScreen: Choose address 
        ManageSolarArrayScreen ->> ManageSolarArrayViewModel: setSearchAddress(address)<br/>setMapAddress(address)
        ManageSolarArrayScreen -->> User: Zoom on the address 

    else Bruker zooms in on an address
        User ->> ManageSolarArrayScreen: Zooms in 
    
        ManageSolarArrayScreen ->> ManageSolarArrayViewModel: queryAddressAtPos(coordinates)
        ManageSolarArrayViewModel ->> BuildingRepository: getNearestAddressToPos(coordinates)
        BuildingRepository ->> BuildingDatasource: getAddressFromPos(coordinates)
        BuildingDatasource ->> GeoNorge: api call
        
        GeoNorge -->> BuildingDatasource: response
        BuildingDatasource -->> BuildingRepository: list of addresses
        BuildingRepository -->> ManageSolarArrayViewModel: nearest address
        ManageSolarArrayViewModel ->> ManageSolarArrayViewModel: setSearchAddress(address)<br/>setMapAddress(address)
    end

    %% henter takflater
    ManageSolarArrayViewModel ->> BuildingRepository: getRoofSections(address)
    BuildingRepository ->> BuildingRepository: getBuildingIds(address)
    BuildingRepository ->> BuildingDatasource: getCadastreId(address)
    BuildingDatasource ->> Kartverket: api kall for cadastreId

    Kartverket -->> BuildingDatasource: http response
    BuildingDatasource -->> BuildingRepository: cadastreId
    
    BuildingRepository ->> BuildingDatasource: getBuildingsIds(cadastreId)
    BuildingDatasource ->> Fjordkraft: api call for buildingids
    Fjordkraft -->> BuildingDatasource: http responce
    BuildingDatasource -->> BuildingRepository: list of buildingids

    BuildingRepository ->> BuildingDatasource: getRoofSections(buildingId)
    BuildingDatasource -->> BuildingRepository: list of roof sections
    BuildingRepository -->> ManageSolarArrayViewModel: list of roof sections
    ManageSolarArrayViewModel -->> ManageSolarArrayScreen:  StateFlow<roof sections>
    ManageSolarArrayScreen -->> User: Highlights roof sections on the address

    %% Brukeren velger takflater
    loop until user is done
        User ->> ManageSolarArrayScreen: Choose roof section
        ManageSolarArrayScreen ->> ManageSolarArrayScreen: make a roof section card<br/>calculate installation price
        ManageSolarArrayScreen -->> User: Show chosen roof sections, card and price
    end 

    %% lagrer
    User ->> ManageSolarArrayScreen: clik on "lagre"
    ManageSolarArrayScreen ->> User: Asks about name and <br/>electricity consumption 
    User ->> ManageSolarArrayScreen: Types name and Saves
    
    par Navigate user 
        ManageSolarArrayScreen ->> User: navigate to home  
    and Saves in database
        create participant SolarArray
        ManageSolarArrayScreen ->> SolarArray: create SolarArray-object 
        SolarArray -->> ManageSolarArrayScreen: SolarArray 

        ManageSolarArrayScreen ->> ManageSolarArrayViewModel: addSolarArray(SolarArray)<br/>setSearchAddress("")
        ManageSolarArrayViewModel ->> SunSaverRepository: addSolarArray(SolarArray)
        SunSaverRepository ->> SunSaverDatasource: insert(SolarArrayWithRoofSections)
        SunSaverDatasource ->> Database(DAO): insertSolarArray(SolarArrayEntity)
        Database(DAO) -->> SunSaverDatasource: id of the solar array 
        SunSaverDatasource ->> Database(DAO): insertRoofSections(List<RoofSectionEntity>)
    end
```
### Tekstlig beskrivelse: 
Pre: Brukeren har trykket på +-tegnet nede i navbaren og er nå dirigert til ManageSolarArrayScreen. <br/>
Post: Solcelleanlegget er lagret i databasen og vises på hjemskjermen. <br/>

1. Bruker trykker på +-tegnet nede for å legge til nytt solcelleanlegg. 
2. Bruker blir navigert til ManageSolarArrayScreen. 
3. Bruker skriver inn en adresse. 
4. Appen gjør et kall mot GeoNorge for å hente adresseforslag. 
5. Bruker velger noe fra forslagene. 
6. Appen gjør et nytt kall mot GeoNorge for å hente hele adressen. (????)
7. Brukeren blir zoomet inn på stedet. 
8. Appen gjør et kall mot Kartverket for å få cadastreId. 
9. Appen gjør et kall mot Fjordkraft for å hente takflater. 
10. Appen markerer takflater på skjermen. 
11. Bruker velger et takflate. 
12. Appen lagrer takflate som kort. Regner ut installasjonsprisen. Viser til brukeren.
13. Bruker trykker på lagre. 
14. Appen ber om å oppgi navn på anlegger og strømforbruk. 
15. Bruker skriver inn navn 
16. Bruker trykker på lagre. 
17. Appen lagrer til databasen og navigerer til HomeScreen. 

 <br/>**Alternativ flyt**: Brukeren velger å zoome inn på adressen manuelt.<br/>

3. Bruker zoomer inn på riktig adresse. <br/>
4. Appen gjør et kall mot GeoNorge for å hente adressen. (???) <br/>
5. Hopp til punkt 8. <br/>

#### Forenklinger/Kommentarer
- Vi starter interaksjon med at brukeren er nettopp blitt navigert til ManageSolarArrayScreen.
- Vi sier at adresseforslag hentes kun en gang selv de egentlig hentes for hver bokstav som skriver/slettes i søkefeltet. 
- Utelatter å forklare alle steg i "appen gjør"-punktene, siden de kan sees i detalj på sekvensdiagrammet. 
- Valideringer/div. brukerinteraksjon etter at adressen er satt skal vises i aktivitetsdiagrammet. Dette er fordi det er lite givende å ha det i sekvensdigrammet, da det er kun interaksjon mellom bruker og ManageSolarArrayScreen-skjermen. 
- Etter at det nye anlegget er lagret, vil databasen automatisk sende ut en oppdatert liste (på grunn av Flow) som HomeScreen vil fange opp. Da vil HomeScreen sette det nye solcelleanlegget i fokus og hente data for den. Dette utelatter vi fra sekvensdiagrammet for å minke kompleksiteten. 

## Use case: Se lagrede solcelleanlegg med data + Velge et anlegg for å se tilhørende data
Sekvensdiagram. Kan ha en aktivitetsdiagram hvor man trykker på ting på skjermen, men strengt tatt ikke nødvendig. <br/>
Velge et anlegg for å se tilhørende data: 
Mulig kan (og bør) kombineres med "Se lagrede solcelleanlegg med data". Kan ha en liten aktivitetsdiagram som viser at man kan ikke velge før data er lastet (men er kanskje ikke nødvendig)

## Use case: Redigere solcelleanlegg
Sekvensdiagram og aktivitetsdiagram. 

## Use case: Slette solcelleanlegg
Vi gir også brukeren mulighet til å slette solcelleanlegg. For dette use caset har vi kun sekvensdiagram fordi å slette et anlegg tar kun ett klikk. 
```mermaid
sequenceDiagram
    actor Bruker
    participant HomrScreem
    participant HomeViewModel
    participant SunSaverRepository
    participant SunSaverDatasource
    participant Database(DAO)

    Bruker ->> HomeScreen: Delete array "hytte"
    HomeScreen ->> HomeViewModel: removeSolarArray(array)
    HomeViewModel ->> SunSaverRepository: deleteSolarArray(array)
	SunSaverRepository ->> SunSaverDatasource: delete(array)
	SunSaverDatasource ->> Database(DAO): delete(array) 
	
	Database(DAO) -->> SunSaverDatasource: updated list 
	SunSaverDatasource -->> SunSaverRepository: updated list
	SunSaverRepository -->> HomeViewModel: updated list
	HomeViewModel -->> HomeScreen: updated list
    
    alt If one than one saved array
        HomeScreen -->> Bruker: show updated list and <br/> focus on the first array
    else User deleted the last array
        HomeScreen -->> Bruker: "Ingen solcelleanlegg er opprettet"
    end
```
Tekstlig beskrivelse: <br/>
Pre: Bruker har minst en (1) solcelleanlegg lagret. <br/>
Post: Den aktuelle solcelleanlegget er slettet. <br/>
Hovedflyt:
1. Bruker klikker på søppelkasse ikonet et lagret solcelleanlegg. 
2. Anlegget slettes fra databasen. 
3. På grunn av Flow blir hjemsiden oppdatert slik at anlegget forsvinner fra lista over lagrede anlegg. 
4. Viser data for det første anlegget som er lagret. 

<br/>Alternativ flyt: Bruker sletter siste anlegg<br/>

4. Viser meldingen "Ingen solcelleanlegg er opprettet"
