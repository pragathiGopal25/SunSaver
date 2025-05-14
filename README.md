# TEAM 54: Sunsaver
- Alexander Paul With
- Amund Hillestad 
- Antonina Bukhantsova 
- Hani Hussein Ali
- Pragathi Gopalakrishnan
- Radwa Ayanle Godor

## Requirements and notices before running the application: 
- The application runs on the Android studio version Ladybug/Meerkat
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
## Calculations 
- Explain how the calculations are made

## Warnings in Logcat:
- Please note that when you run our app, you will see several logcat warnings from MapBox, but dont worry these are to be expected. The app functions correctly, however the warnings occur due to the fact that Mapbox is not able to manage the activity lifecyle regarding annotations and drawings on the map well. Therefore, it displays warnings indicating that it was not possible to delete certain annotations despite everything functioning well. This is something that might be fixed in the future, however at the time of this submission these warnings will still appear.
