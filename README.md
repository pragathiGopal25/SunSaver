# TEAM 54: Sunsaver
- Alexander Paul With
- Amund Hillestad 
- Antonina Bukhantsova 
- Hani Hussein Ali
- Pragathi Gopalakrishnan
- Radwa Ayanle Godor

## Requirements and notices before running the application: 
- The application runs on the Android studio versions Ladybug/Meerkat
- In order to use the application you need access to the internet.
- Please note that you cannot type norwegian characters Æ, Ø and Å on your computers keyboard when using the app. However, it is possible to write these characters using the keyboard in the emulator.

## How to run the application: 
In order to run the application, do the following steps:

1. Download the project files from the GitHub repository   
2. Open these files in Android Studio  
3. Sync Gradle  
4. Choose "Resizable (Experimental) API 34" as the device  
5. Run the app!

## Libraries used:
- [Vico - compose](https://www.patrykandpatrick.com/vico/guide/stable)
    - This is the library we used to make/design the graph in our app. This is referenced in the file, ElectricityGraph. We decided to implement vico - compose due to its wide range of graph options. 
- [Room](https://developer.android.com/jetpack/androidx/releases/room)
    - We used the Room database to store solar arrays and their respective roof sections in our app.
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
    - We utilised Hilt for dependency injection in our code. 
- [Mapbox](https://docs.mapbox.com/android/maps/guides/)
    - This is the library we used to access and display the map in our app.
- [LottieFiles](https://lottiefiles.com/blog/working-with-lottie-animations/getting-started-with-lottie-animations-in-android-app)
    - We used LottieFiles to create and implement our animated splash screen. 
- [Ktor](https://ktor.io/docs/welcome.html)
    - We used Ktor for making HTTP connections/requests for API calls to Frost, Fjordkraft, Kartverket, Geonorge and HvaKosterStrommen.
- [Jetpack Compose](https://developer.android.com/develop/ui/compose/documentation)
    - We used Jetpack Compose to design and build the UI components in our app.
- [JUnit](https://developer.android.com/training/testing/instrumented-tests/androidx-test-libraries/runner)
    - We used JUnit for testing. 

## Calculations 
- Our calculation of the solar energy produced was based on the following formula:
> E = A * r * H * PR  
- The formula is credited to [photovoltaic - software.com](https://photovoltaic-software.com/principle-ressources/how-calculate-solar-energy-power-pv-systems)
- According to the formula, the energy (E) produced by solar panels is equivalent to the product of the area (A) of the panels, the panel efficiency (r), annual average solar radiation (H) and the performance ratio (PR).

- That was the basic structure of the formula used in our calculations, however we had to alter and make some assumptions regarding some of the variables in the formula. 

- Firstly, in order to compute the formula, we collected the following data from frost:
    - Surface Downelling shortwave flux (hourly)
    - Sum duration of sunshine (sum of sunhine in a month)
    - Snow coverage type (monthly)
    - Cloud area fraction (daily)
    - Mean air temperature (monthly)
- **Assumptions**:
    - Performance Ratio: According to the source provided, the performance ratio is a percentage that is determined by various things, including temperature losses, loss due to snow, clouds etc. In our calculations, instead of representing this as one number, we have a variety of values that represent it. This includes: direction impact, angle impact, snow loss factor and cloud loss factor. It is to be noted that snow loss and cloud factors are taken into consideration when calculating/adjusting the solar irradiation value from frost. According to further research on the effect of the aforementioned variables we have assumed the level of impact they have on the total solar energy produced. 
        - For example, we have assumed that in the winter months (due to the abundance of snow) snow has a larger impact on the total irradiance. Hence, in a summer month, a snow coverage type of 1 would reduce the irradiance to 98% of its original value, whereas in a winter month this value would reduce to 92% [(Pawluk et al., 2019)](https://doi.org/10.1016/j.rser.2018.12.031 )
        - We have made similar assumptions for the impact of clouds, angle of the roof and its direction. 
    - Solar radiation (H): According to the formula, this is the solar radiation on tilted solar panels. We have ustilised the "Surface downwelling shortwave flux" data from frost which represents the total incoming solar radiation on a horizontal surface inlcuding both direct and diffuse shortwave radiation.  We have adjusted that value to account for the impact of snow and clouds. 
    - Efficiency (r): For the efficiency of the solar panel, we utilised a formula from [photonicuniverse.com](https://www.photonicuniverse.com/en/resources/articles/full/7.html) 
        > Efficiency =  panel power (kW) / panel area 
    - In addition to this formula we also adjusted the efficiency based on the air temperature. According to research, panel efficiency can decrease if the temperatures are above 25 degrees celsius, this is called the temperature coefficient and has a value between 0.25 - 0.5 [(Solceller – Store Norske Leksikon, 2024)](https://snl.no/solceller). Additionally, for temperatures below that value, efficiency can increase by the same coefficient. In order to stay within the range we decided to use a temperature coefficient of 0.3.
- Area of solar panels: Due to the limited information on the dimensions of the solar panels provided by Fjordkraft (the company that provides our buildings and roof section API), we have assumed average panel dimensions based on the typical size for panels of the specified power rating. This has allowed us to estimate the panel area in our calculations. 


NB: For further readings on the references please refer to the bibliography in the report.


## Warnings in Logcat:
- Please note that when you run our app, you will see several logcat warnings from MapBox, but dont worry these are to be expected. The app functions correctly, however the warnings occur due to the fact that Mapbox is not able to manage the activity lifecyle regarding annotations and drawings on the map well. Therefore, it displays warnings indicating that it was not possible to delete certain annotations despite everything functioning well. This is something that might be fixed in the future, however at the time of this submission these warnings will still appear.
