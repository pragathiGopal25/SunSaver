## Legge til nytt anlegg 
Preconditions: Bruker har åpnet appen. Ingen anlegg lagret.  
```mermaid
sequenceDiagram
    actor Bruker
    participant HomeScreen as Home
    participant ManageSolarArrayScreen as AddScreen 
    participant ManageSolarArrayViewModel 
    participant BuildingRepository 
    participant BuildingDatasource
    participant SunSaverRepository
    participant SunSaverDatasource 
    participant Database(DAO)


    Bruker ->> HomeScreen: Trykker på + i navbar
    HomeScreen ->> ManageSolarArrayScreen: navigerer til
    ManageSolarArrayScreen -->> Bruker: Viser kart og søkefelt for adresser
    
    %% adresse forslag
    Bruker ->> ManageSolarArrayScreen: Skriver inn adresse
    ManageSolarArrayScreen ->> ManageSolarArrayViewModel: setMapAddress(adresse)
    ManageSolarArrayViewModel ->> BuildingRepository: getAddressSuggestions(adresse)
    BuildingRepository ->> BuildingDatasource: getAddressSuggestions(adresse)
    BuildingDatasource ->> GeoNorge: api kall for adresser
    GeoNorge -->> BuildingDatasource: http responce
    BuildingDatasource -->> BuildingRepository: liste av adresser 
    BuildingRepository -->> ManageSolarArrayViewModel: liste av adresser
    ManageSolarArrayViewModel -->> ManageSolarArrayScreen: StateFlow av adresseforslag 
    ManageSolarArrayScreen -->> Bruker: Viser adresseforslag 

    Bruker ->> ManageSolarArrayScreen: Velger adresse 
    
    %% henter søkt adresse
    ManageSolarArrayScreen ->> ManageSolarArrayViewModel: setSearchAddress(adresse)<br/>setMapAddress(adresse)
    ManageSolarArrayScreen -->> Bruker: Zoomer inn på stedet 
    ManageSolarArrayViewModel ->> BuildingRepository: getRoofSections(adresse)

    BuildingRepository ->> BuildingRepository: getBuildingIds(adresse)
    
    BuildingRepository ->> BuildingDatasource: getCadastreId(adress)
    BuildingDatasource ->> Kartverket: api kall for ?? 
    Kartverket -->> BuildingDatasource: http response
    BuildingDatasource -->> BuildingRepository: cadastreId?
    
    BuildingRepository ->> BuildingDatasource: getBuildingsIds(cadastreId)
    BuildingDatasource ->> Fjordkraft: api kall for ?? 
    Fjordkraft -->> BuildingDatasource: http responce
    BuildingDatasource -->> BuildingRepository: liste av buildingids

    BuildingRepository ->> BuildingDatasource: getRoofSections(buildingId)
    BuildingDatasource -->> BuildingRepository: Liste av takflater
    BuildingRepository -->> ManageSolarArrayViewModel: Liste av takflater
    ManageSolarArrayViewModel -->> ManageSolarArrayScreen:  StateFlow<MapRoofSectionsState>
    ManageSolarArrayScreen -->> Bruker: Markerer takflater 

    Bruker ->> ManageSolarArrayScreen: Velger takflater 
    ManageSolarArrayScreen -->> Bruker: Viser markerte takflater på kartet<br/>Legger til takflate-kort

    Bruker --> ManageSolarArrayScreen: Trykker på lagre
    ManageSolarArrayScreen --> Bruker: Ber om å gi navn til anlegget<br/>og oppgi strømforbruk
    Bruker --> ManageSolarArrayScreen: Skriver inn navn og trykker på lagre 
    
    create participant SolarArray
    ManageSolarArrayScreen --> SolarArray: Lag ny SolarArray-objekt 
    SolarArray -->> ManageSolarArrayScreen: Lagd 

    ManageSolarArrayScreen ->> ManageSolarArrayViewModel: addSolarArray(SolarArray)
    ManageSolarArrayViewModel ->> SunSaverRepository: addSolarArray(SolarArray)
    SunSaverRepository ->> SunSaverDatasource: insert(SolarArrayWithRoofSections)
    SunSaverDatasource ->> Database: insertSolarArray(SolarArrayEntity)
    Database -->> SunSaverDatasource: id til solar array 
    SunSaverDatasource ->> Database(DAO): insertRoofSections(List<RoofSectionEntity>)

    ManageSolarArrayScreen --> ManageSolarArrayViewModel: setSearchAddress("")
    ManageSolarArrayScreen --> Bruker: naviger til hjemskjermen 


```


alt flyt: 
- bruker zoomer inn på kartet selv istedenfor å søke opp 
- bruker skal skrive inn og glemmer et felt 

alt/opt: 
- skriver inn takflateinfo 
- endrer takflate info 

Kanskje legge til at lagringen til db skjer samtidig som resten av ting på skjermen? siden det er asykron ting