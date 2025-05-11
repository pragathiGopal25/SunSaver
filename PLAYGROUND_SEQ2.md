## Se lagrede anlegg og velg et annet anlegg
Vi slår sammen sekvensdiagrammene for disse to use cases fordi flyten i de er ganske lik, så det er logisk å slå disse to sammen. <br/>
Blant det vi ønsker å vise med dette sekvensdiagrammet, er at hvis man trykker på et anlegg som har statistikk lastet opp, så vil det ikke lastes opp ijgen. Det som vi derimot ikke viser, men er viktig her, er at hvis man trykker på et anlegg som har tidligere feilet, vil det prøve å laste data opp på nytt. 

```mermaid
sequenceDiagram
    actor Bruker
    participant HomeScreen
    participant HomeViewModel 
    participant FrostRepository 
    participant FrostDatasource
    participant Frost
    participant elRep as ElectricityPriceRepository 
    participant elDat as ElectricityPriceDatasource
    participant hks as HvaKosterStrømmen 
    participant SunSaverRepository 
    participant SunSaverDatasource
    participant Database 

    HomeViewModel ->> SunSaverRepository: getAllSolarArrays()
    SunSaverRepository ->> SunSaverDatasource: getAllSolarArrays()
    SunSaverDatasource ->> Database: getAllSolarArrays()

    Database -->> SunSaverDatasource: liste av anlegg
    SunSaverDatasource -->> SunSaverRepository: liste av anlegg
    SunSaverRepository -->> HomeViewModel: liste av anlegg
    HomeViewModel -->> HomeViewModel: selectSolarArray(anlegg)
    par Hva bruker ser
        HomeViewModel -->> HomeScreen: første anlegg valgt,<br/>henter data
        HomeScreen -->> Bruker: visuell feedback at data lastes
    and Hvordan data hentes og prosesseres
        HomeViewModel ->> HomeViewModel: getPriceData(anlegg)<br/>getWeatherData(anlegg)
        
        par Henter data fra HvaKosterStrømmen 
            HomeViewModel ->> elRep: getPriceArea(koord)
            elRep -->> HomeViewModel: prissone
            loop For 5/30/365 dager 
                loop en iterasjon er <br/> en dag med strømpriser
                    HomeViewModel ->> elRep: getPriceDataInterval(dager, sone)
                    elRep ->> elDat: getElectricityPrices(sone, dato)
                    elDat ->> hks: hent data for en dag

                    hks -->> elDat: priser time for time
                    elDat -->> elRep: priser per dag 
                    elRep --> elDat: gjennomsnittspris
                end
            end
            
        and Henter data fra Frost 
            loop For hver element 
                par asykrone kall 
                    HomeViewModel ->> FrostRepository: getData(koord, element)
                    FrostRepository ->> FrostDatasource: fetchObservationDataFromFrost(koord, element)

                    FrostDatasource ->> FrostDatasource: fetchNearestSource(koord, element)
                    FrostDatasource ->> Frost: hent 5 nærmeste sensorer
                    Frost -->> FrostDatasource: sensorer

                    FrostDatasource ->> Frost: av de 5 nærmeste, hvilke har <br/> data i riktig tidsintervall 
                    Frost -->> FrostDatasource: sensorer 
                    FrostDatasource ->> Frost: hent data fra sensoren
                    Frost ->> FrostDatasource: data

                    FrostDatasource -->> FrostRepository: all data
                    FrostRepository ->> FrostRepository: getMonthlyAverageValues(data)
                    FrostRepository -->> HomeViewModel: gjennomsnittslige verdier <br/> mer måned 
                end
            end
        end

        HomeViewModel ->> HomeViewModel: useWeatherData(anlegg)
        HomeViewModel -->> HomeScreen: vis grafen
        HomeViewModel ->> HomeViewModel: loadElectricityPrices(anlegg)
        HomeViewModel ->> elRep: getPriceData(dager, gen_av_anlegg,<br/> forbruk, avg_pris)
        elRep -->> HomeViewModel: sparing med og uten solceller
        HomeViewModel ->> HomeViewModel: seePrices(anlegg)<br/>calculateRecoup(anlegg)
    end
    HomeViewModel -->> HomeScreen: vis sparing og inntjening statistikk
    

```
### Tekstlig beskrivelse: 
Pre: Bruker har minst to lagrede anlegg. Bruker går inn på appen. <br/>
Post: Bruker fikk sett på anleggene sine. <br/>
1. Appen henter lagrede anlegg fra databasen og viser dem på HomeScreen. 
2. Mens data hentes, vises "laster" animasjonen. 
3. Det første anlegget er automatisk i fokus. 
4. Appen henter data fra Frost og HvaKosterStrømen og viser det i form av Sparing-, Strømproduksjon og Inntjenning-komponentene. 
5. Bruker velger en annen solcelleanlegg. 
6. Appen henter data fra Frost og HvaKosterStrømen og viser det i form av Sparing-, Strømproduksjon og Inntjenning-komponentene for dette anlegget.
7. Bruker trykker igjen på det første anlegget. 
8. Appen viser statistikken for dette anlegget uten noen ekstra kall til API-ene. 

<br/>**Alternativ flyt**:.<br/>
Bruker prøver å velge et anlegg mens data lastes. <br/>
Bruker klikker seg rundt i Sparing-boksen. <br/>
Greier ikke å hente data fra frost 

#### Forenklinger/Kommentarer
- Bruker "koord" for "koordinater" for å spare litt plass
- He