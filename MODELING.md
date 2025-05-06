# Modellering
### Inkluderte diagrammer: 
- Use case diagram: Gir en oversikt over de viktigste funksjonene 
i appen sett fra brukerens perspektiv, og hvilke handlinger brukeren kan utføre.
- Klassediagram: viser hvilke klasser og datamodeller, og hvordan de henger sammen.
- Sekvensdiagrammer: for utvalgte/hver use case viser hvordan de ulike komponentene 
(fra klassediagrammet) kommuniserer sammen for å utføre use caset
- Aktivitetsdiagrammet: for utvalgte/hver use case viser hvordan interaksjonen 
ser ut fra brukerens perspektiv 


## Use case diagram 
![Use case diagram ofr SunSaver](image.png)
Diagrammet ble laget ved hjelp av [app.diagrams.net](https://app.diagrams.net/) 
siden Mermaid ikke tilbyr Use case diagrammer. <br>

## Klassediagram

```mermaid
classDiagram
    class SolarArray {
        +id: Long
        +name: String
        +panelType: SolarPanelType
        +roofSections: RoofSection
        +coordinates: Coordinates
        +powerConsumption: Double
        +address: String
    }

    class RoofSection {
        +id: Long?
        +area: Double
        +incline: Double
        +direction: Double
        +panels: Int
        +mapId: String?
    }

    class Coordinates {
        +latitude: Double
        +longitude: Double
        +toPoint() Point
    }

    class SolarPanelType {
    <<Enumeration>>
    +ECONOMY
    +PERFORMANCE
    +PREMIUM
    --
    +displayName: String
    +watt: Int
    +price: Double 
    +installationPrice: Double
    +length: Double
    +width: Double
    +totalPrice(amount Int) Double
    +nameWithWatt() String
    +area() Double
  }
    SolarArray "1" --> "1..*" RoofSection : roofsections
```