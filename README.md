# OutfitKatya

This is a [Kotlin](https://kotlinlang.org/) project with Android SDK done. <b>I use in that's project:</b> MATERIAL DESIGN, OKHTTP3, GSON, VIEWMODEL WITH LIFECYCLE, FOREGROUND SERVICE, SERVICE, COROUTINES

## Getting Started

Unfortunately, this application will not start when you load to Android Studio. The reason is the missing two files: first with const database and google-services.json. But I'm working on the test server for this app.

## Information

Project was created for company [Katya RG Leotards](https://katya-rg.eu/) for helps in  management the creating costumes. This app is connected with database and read from API informations about time of creating costumes, the names and who working on it. 

Thanks of inbuilt Countdown Timer the app allows direct insertion the times to database.

## Technology

App has inbuild Countdown Timer which use [Service](https://developer.android.com/guide/components/services) and [Foreground Service](https://developer.android.com/guide/components/foreground-services) from android library. 

To communication between Service and activities is used [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) from Jetpack library and lifecycle.

It is using [okhttp3](https://square.github.io/okhttp/) and [Gson](https://github.com/google/gson) to decipher and communication with database.

## How it's work

After turning on the app it shows up list of outfits. In that place it can add and remove outfits. It is possible to set as finished outfit.

<p align="center">
<img src="https://user-images.githubusercontent.com/65240240/198519693-be54a4a9-c522-45f1-9034-849ceb21b24a.png" alt="startImage" width="600"/>
</p>

On the click in anyone outfit show us Activity with Fragments. The first fragment lets us turn on a Stopwatch or add new manual entry time.

<p float="left">
<img src="https://user-images.githubusercontent.com/65240240/198520101-ae760fa6-0d40-44a2-83ad-0dd133c3cdae.png" alt="stopwatchImage" width="500"/>
 <img src="https://user-images.githubusercontent.com/65240240/198520540-8036584e-b116-4e94-b907-f670780f29bb.png" alt="stopwatchRunningImage" width="500"/>
</p>



After scroll down show up two tabs: Mom and Katya. There are times of working for this two people. When we scroll to the left, this time will be delete. Of course everything is security with DialogAlert. On the down app is summarize of time.

<p align="center">
<img src="https://user-images.githubusercontent.com/65240240/198520748-d85e9df8-787e-45ac-9b9c-6dddd84d123c.png" alt="stopwatchRunningImage" width="600"/>
</p>
