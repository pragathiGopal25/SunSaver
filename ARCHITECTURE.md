# ARCHITECTURE

### Model View ViewModel (MVVM) and Unidirectional Data Flow (UDF)

The app is built and developed on the premise of [Recommendations for Android architecture](https://developer.android.com/topic/architecture/recommendations), where there is an emphasis on a layered architecture. This is also reflected in the folder structure, which is separeted into models, data sources, repositories, and UI. Furthermore, we have followed this principle by implementing `Model-View-ViewModel` to seperate the UI layer from the business logic, while communicating between the two layers through a viewmodel. In our case the "Model" is our repositories and data sources, while the "View" is our Composables. This isolates the different components to focus on a single task, which, in practice, means that we try to achieve high cohesion and low coupling while seperating the UI and Model layers.

> _Coupling_: `ManageSolarArrayScreen` is only dependent on `SolarArrayViewModel`, and not any other business logic or viewmodels. Because the class only has one dependency, it has low coupling.

> _Cohesion_: `ManageSolarArrayViewModel` only works as the connection between the model layer and `ManageSolarArrayScreen`. Events trigger methods that updates the model layer, and the viewmodel makes sure that the UI reflects the changes made in the model layer. Thus the class has high cohesion, because it has a well defined job.

We also follow the principles of [Unidirectional Data Flow](https://developer.android.com/topic/architecture#unidirectional-data-flow), where the state only flows in one direction, while the events flow in the opposite direction. In our case, this is implemented in how an event can be triggered by a user in a Composable, and this event alters the state in the viewmodel. Afterwords, the viewmodel updates the UI to reflect the current state. To follow the correct state-flow is important to avoid errors or bugs where the UI doesn't reflect the current state.

> _MVVM/UDF_: The user adds a new solar array (event), which in turn calls a function from `selectSolarArray()` where it loads data from the repositories and data sources, as well as updates `homeUiState` to reflect the new data. The composable now collects the updated UI state, and updates the Composable to now show information about the selected solar array.

The `HomeViewModel` also observes the Room database with the `collect()` method, where it updates the UI layer based on if there are changes made to the saved solar arrays in the database. In this case, we are using an observer pattern where `HomeViewModel` is the observer, and `Room` is the subject. However, the database still follows the `UDF` principles, where it updates based on the usual event driven flow.

## Further developement

### API

Minimun SDK: 24
By using API level 24, we are able to cover [97%](https://apilevels.com) of Androids cumulative usage while still being able to use all of the libraries that we needed. Because of this we didn't see the need of using a newer Android version.

Target SDK: 34
Google Play requires that [apps target SDK version 34 or higher](https://developer.android.com/google/play/requirements/target-sdk), which we thought was a nice guideline to follow, even though we have no intention of publishing the app to the Google Play Store. However, with the fact that we follow this guideline, we would have the theoretical option of transitioning the project to be published on the Google Play Store.

### Technical debt

The app uses historical data (both for electricity prices and weather information). While we want the most up to date data to be used in calculations, we dont need to always get all of the data every time reload the app. To solve this issue, the data could be saved in the `Room database`, and only if there is newer data to retrieve should it make more API calls.

If the app were to have even more functionality without resolving this issue first, there is a chance that perfomance would become worse and worse. If this issue is resolved, either as proposed or in another way, the app should be more efficient, and therefore also better suited for further developement.

## Libraries and APIs

The project includes the usage of the following APIs:

- [Mapbox Maps](https://docs.mapbox.com/api/maps/)
- [Kartverket adresse](https://ws.geonorge.no/adresser/v1/)
- [Hvakosterstrommen](https://www.hvakosterstrommen.no)
- [Frost](https://frost.met.no/index.html)
- [Fjordkraft](https://sol.fjordkraft.no) \*

The project uses the following libraries:

- [Ktor](https://ktor.io)
- [Room](https://developer.android.com/jetpack/androidx/releases/room)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [Lottiefiles](https://lottiefiles.com/blog/working-with-lottie-animations/getting-started-with-lottie-animations-in-android-app)
- [Vico](https://github.com/patrykandpatrick/vico) \*
- [Jetpack Compose](https://developer.android.com/compose)
- [JUnit](https://developer.android.com/training/testing/instrumented-tests/androidx-test-libraries/runner)

### Fjordkraft

We use the Fjordkraft API to load the roof sections of any house in Norway. This is not a public API, and is only allowed to be used in the context of our studies and is not intended in any way for commercial use.

### Vico

Vico is scheduled to add [Initial accessability support](https://github.com/patrykandpatrick/vico/pull/1069) very soon, which means this library will solve the current issue of lacking accessability by introducing `contentDescription` as a field. This will make the screen reader able to read graph values. Even though the library in its current state is somewhat lacking, it is still being developed and should therefore be usable in the future developement of this project as well.
