## Se lagret anlegg

```mermaid
sequenceDiagram
    actor User
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

    Database -->> SunSaverDatasource: list of arrays
    SunSaverDatasource -->> SunSaverRepository: list of arrays
    SunSaverRepository -->> HomeViewModel: list of arrays
    HomeViewModel -->> HomeViewModel: selectSolarArray(array)
    par What user sees
        HomeViewModel -->> HomeScreen: chosen array,<br/> retrieving data message
        HomeScreen -->> User: visual feedback 
    and Fetching and processing of data
        HomeViewModel ->> HomeViewModel: getPriceData(array)<br/>getWeatherData(array)
        
        par Fetching data from HvaKosterStrømmen
            HomeViewModel ->> elRep: getPriceArea(coord)
            elRep -->> HomeViewModel: price area
            loop For 5/30/365 days 
                loop need an api call for each day
                    HomeViewModel ->> elRep: getPriceDataInterval(day, area)
                    elRep ->> elDat: getElectricityPrices(are, date)
                    elDat ->> hks: fetch data for one day

                    hks -->> elDat: prices per hour
                    elDat -->> elRep: prices per day 
                    elRep ->> elDat: avg. prices
                end
            end
            
        and Fetching data from Frost 
            loop For each element 
                par asycrone calls 
                    HomeViewModel ->> FrostRepository: getData(coord, element)
                    FrostRepository ->> FrostDatasource: fetchObservationDataFromFrost(coord, element)

                    FrostDatasource ->> FrostDatasource: fetchNearestSource(coord, element)
                    FrostDatasource ->> Frost: fetch 5 nearest sensors
                    Frost -->> FrostDatasource: sensors

                    FrostDatasource ->> Frost: given 5 nearest, which do <br/>have data in our time interval 
                    Frost -->> FrostDatasource: sensors 
                    FrostDatasource ->> Frost: fetch data from the nearest sensor
                    Frost ->> FrostDatasource: data

                    FrostDatasource -->> FrostRepository: all data
                    FrostRepository ->> FrostRepository: getMonthlyAverageValues(data)
                    FrostRepository -->> HomeViewModel: avg. per month 
                end
            end
        end

        HomeViewModel ->> HomeViewModel: useWeatherData(array)
        HomeViewModel -->> HomeScreen: show graph
        HomeViewModel ->> HomeViewModel: loadElectricityPrices(array)
        HomeViewModel ->> elRep: getPriceData(days, generated_by_array,<br/> usage, avg_price)
        elRep -->> HomeViewModel: saving with and without arrays
        HomeViewModel ->> HomeViewModel: seePrices(array)<br/>calculateRecoup(array)
    end
    HomeViewModel -->> HomeScreen: show saving statistic 
```

### Tekstlig beskrivelse: 
Pre: Bruker har minst et lagret anlegg. Bruker går inn på appen. <br/>
Post: Bruker fikk sett statistikk for sin anlegg. <br/>
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
- Flyten her er ganske komplisert, så her kommer det en ekstra forklaring: bla bla