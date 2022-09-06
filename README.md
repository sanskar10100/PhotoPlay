# PhotoPlay
A simple Android app that helps you explore movies and build your watchlist.

## ScreenCap
![PhotoPlay](https://user-images.githubusercontent.com/22092047/187370949-be36e3ab-f808-47fd-a2d0-e2f83f792ac5.jpeg)

## Major Components
- Kotlin (+Coroutines and Flow)
- Android SDK 33
- Jetpack Compose with Fragment Interop (top level screens are fragments)
- MVVM with Repository
- Dependency Injection using Hilt
- Navigation Component (fragments)
- [Lottie](https://github.com/airbnb/lottie)
- [Coil](https://github.com/coil-kt/coil)
- Retrofit
- Moshi
- Room
- Custom Paging
- Compose Accompanist (Pager)
- [logcat](https://github.com/square/logcat)
- [SplashScreen Compat](https://developer.android.com/develop/ui/views/launch/splash-screen/migrate#migrate_your_splash_screen_implementation)

## Build and Run
- Clone the repo
- Open in Android Studio
- Obtain an API key from [TMDB](https://developers.themoviedb.org/3), and add it in your `local.properties` with the name `API_KEY`
```aidl
API_KEY="YOUR_API_KEY" // Don't forget the quotes
```
- Build and run
- Alternatively, download the latest APK from the assets on the [releases](https://github.com/sanskar10100/PhotoPlay/releases) page