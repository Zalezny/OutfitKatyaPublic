# OutfitKatya

This is a [Kotlin](https://kotlinlang.org/) project with Android SDK done. <b>I use in that's project:</b> MATERIAL DESIGN, OKHTTP3, GSON, VIEWMODEL WITH LIFECYCLE, FOREGROUND SERVICE, SERVICE, COROUTINES, ROOM DATABASES (SQL)

## Getting Started

<!--~~Unfortunately, this application will not start when you load to Android Studio. The reason is the missing two files: first with const database and google-services.json. But I'm working on the test server for this app.~~-->

The application was transformed from online database to a local database (using Room) to show how the application works. You can easily clone that's repository and test it. In files have been commented out code for online database (it is useless without const of database online).  

## Information

Project was created for company [Katya RG Leotards](https://katya-rg.eu/) for helps in  management the creating costumes. This app is connected with database and read from API informations about time of creating costumes, the names and who working on it. 

Thanks of inbuilt Countdown Timer the app allows direct insertion the times to database.

## Technology

App has inbuild Countdown Timer which use [Service](https://developer.android.com/guide/components/services) and [Foreground Service](https://developer.android.com/guide/components/foreground-services) from android library. 

To communication between Service and activities is used [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) from Jetpack library and lifecycle.

It is using [okhttp3](https://square.github.io/okhttp/) and [Gson](https://github.com/google/gson) to decipher and communication with database.

To create local database was used [Room](https://developer.android.com/jetpack/androidx/releases/room).

## How it's work

After turning on the app it shows up list of outfits. In that place it can add and remove outfits. It is possible to set as finished outfit.

<p align="center">
<img src="https://github.com/Zalezny/OutfitKatyas/assets/65240240/f8f6a42d-d65c-4e5e-8c75-ee87afc736db" alt="startImage" width="300" height="650"/>
</p>

On the click in anyone outfit show us Activity with Fragments. The first fragment lets us turn on a Stopwatch or add new manual entry time.

<p float="left" align = "center">
<!---<img src="https://user-images.githubusercontent.com/65240240/198520101-ae760fa6-0d40-44a2-83ad-0dd133c3cdae.png" alt="stopwatchImage" width="500"/>
// <img src="https://user-images.githubusercontent.com/65240240/198520540-8036584e-b116-4e94-b907-f670780f29bb.png" alt="stopwatchRunningImage" width="500"/>-->
 <img src="https://github.com/Zalezny/OutfitKatyas/assets/65240240/b9d5c4fb-f8a0-4edc-bf83-8fce92fecb2a" alt="stopwatchImage" width="300"/>
 &nbsp;
 <img src="https://github.com/Zalezny/OutfitKatyas/assets/65240240/898382f0-d2a8-43b7-9fa0-f70448d272f7" alt="stopwatchRunningImage" width="300" />
</p>



After scroll down show up two tabs: Mom and Katya. There are times of working for this two people. When we scroll to the left, this time will be delete. Of course everything is security with DialogAlert. On the down app is summarize of time.

<p align="center">
<!-- <img src="https://user-images.githubusercontent.com/65240240/198520748-d85e9df8-787e-45ac-9b9c-6dddd84d123c.png" alt="stopwatchRunningImage" width="600"/> -->
 <img src="https://github.com/Zalezny/OutfitKatyas/assets/65240240/c441a788-9fec-43d3-b9be-981249fe3909" alt="stopwatchRunningImage" width="300" />
</p>

