## Legge til nytt anlegg 
Preconditions: Bruker har åpnet appen. Ingen anlegg lagret.  
```mermaid
sequenceDiagram

    %% deklarerer medvirkende
    actor Bruker
    participant ManageSolarArrayScreen as AddScreen 
    participant ManageSolarArrayViewModel 
    participant BuildingRepository 
    participant BuildingDatasource
    participant GeoNorge
    participant Kartverket 
    participant Fjordkraft
    participant SunSaverRepository
    participant SunSaverDatasource 
    participant Database(DAO)

    ManageSolarArrayScreen -->> Bruker: Viser kart og søkefelt for adresser

    alt Bruker søker på en adresse
        %% adresse forslag
        Bruker ->> ManageSolarArrayScreen: Skriver inn en adresse
        ManageSolarArrayScreen ->> ManageSolarArrayViewModel: setMapAddress(adresse)
        ManageSolarArrayViewModel ->> BuildingRepository: getAddressSuggestions(adresse)
        BuildingRepository ->> BuildingDatasource: getAddressSuggestions(adresse)
        BuildingDatasource ->> GeoNorge: api kall for adresser
        GeoNorge -->> BuildingDatasource: http responce
        BuildingDatasource -->> BuildingRepository: liste av adresser 
        BuildingRepository -->> ManageSolarArrayViewModel: liste av adresser
        ManageSolarArrayViewModel -->> ManageSolarArrayScreen: StateFlow av adresseforslag 
        ManageSolarArrayScreen -->> Bruker: Viser adresseforslag 

        %% bruker velger adresse
        Bruker ->> ManageSolarArrayScreen: Velger adresse 
        ManageSolarArrayScreen ->> ManageSolarArrayViewModel: setSearchAddress(adresse)<br/>setMapAddress(adresse)
        ManageSolarArrayScreen -->> Bruker: Zoomer inn på stedet 

    else Bruker zoomer inn på kartet
        Bruker ->> ManageSolarArrayScreen: zoomer inn på kartet 
    
        ManageSolarArrayScreen ->> ManageSolarArrayViewModel: queryAddressAtPos(koordinater)
        ManageSolarArrayViewModel ->> BuildingRepository: getNearestAddressToPos(koordinater)
        BuildingRepository ->> BuildingDatasource: getAddressFromPos(koordinater)
        BuildingDatasource ->> GeoNorge: api kall
        
        GeoNorge -->> BuildingDatasource: response
        BuildingDatasource -->> BuildingRepository: liste av adresser
        BuildingRepository -->> ManageSolarArrayViewModel: nærmeste adresse/null
        ManageSolarArrayViewModel ->> ManageSolarArrayViewModel: setSearchAddress(adresse)<br/>setMapAddress(adresse)
    end

    %% henter takflater
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
    ManageSolarArrayScreen -->> Bruker: Markerer takflater på adressen

    %% Brukeren velger takflater
    loop Til brukeren er ferdig
        Bruker ->> ManageSolarArrayScreen: Velger takflater
        ManageSolarArrayScreen ->> ManageSolarArrayScreen: Lager et takflatekort<br/>Beregner installasjonsprisen.
        ManageSolarArrayScreen -->> Bruker: Viser valgt takflate, takflatekort og installasjonsprisen. 
    end 

    %% lagrer
    Bruker ->> ManageSolarArrayScreen: Trykker på lagre
    ManageSolarArrayScreen ->> Bruker: Ber om å gi navn til anlegget<br/>og oppgi strømforbruk
    Bruker ->> ManageSolarArrayScreen: Skriver inn navn og trykker på lagre 
    
    par Navigerer brukeren 
        ManageSolarArrayScreen ->> Bruker: naviger til hjemskjermen 
    and Lagrer til databasen 
        create participant SolarArray
        ManageSolarArrayScreen ->> SolarArray: Lag ny SolarArray-objekt 
        SolarArray -->> ManageSolarArrayScreen: Lagd 

        ManageSolarArrayScreen ->> ManageSolarArrayViewModel: addSolarArray(SolarArray)<br/>setSearchAddress("")
        ManageSolarArrayViewModel ->> SunSaverRepository: addSolarArray(SolarArray)
        SunSaverRepository ->> SunSaverDatasource: insert(SolarArrayWithRoofSections)
        SunSaverDatasource ->> Database: insertSolarArray(SolarArrayEntity)
        Database -->> SunSaverDatasource: id til solar array 
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


 <br/>**Alternativ flyt**: Brukeren velger å zoome inn på adressen manuelt.<br/>

3. Bruker zoomer inn på riktig adresse. <br/>
4. Appen gjør et kall mot GeoNorge for å hente adressen. (???) <br/>
5. Hopp til punkt 8. <br/>

#### Forenklinger/Kommentarer
- Vi starter interaksjon med at brukeren er nettopp blitt navigert til ManageSolarArrayScreen.
- Vi sier at adresseforslag hentes kun en gang selv de egentlig hentes for hver bokstav som skriver/slettes i søkefeltet. 
- Utelatter å forklare alle steg i "appen gjør"-punktene, siden de kan sees i detalj på sekvensdiagrammet. 
- Valideringer/div. brukerinteraksjon etter at adressen er satt skal vises i aktivitetsdiagrammet. Dette er fordi det er lite givende å ha det i sekvensdigrammet, da det er kun interaksjon mellom bruker og ManageSolarArrayScreen-skjermen. 