# ShareTheImage

An offline-first image discovery application built with Jetpack Compose, Hilt, Room, and Ktor, using the Unsplash API.

## Build Instructions

1. **API Key Configuration**:
   The app uses the Unsplash API. The access key is injected via `buildConfigField`.
   You should add your API Key in `local.properties` (which is gitignored) at the root of the project:
   ```properties
   UNSPLASH_ACCESS_KEY=your_access_key_here
   ```

2. **Build**:
   Open the project in Android Studio and sync Gradle.
   Run the `app` configuration on an emulator or device.

## Architecture Decisions

The application follows **Clean Architecture** and **MVVM** principles with a **Single Source of Truth** (SSOT) pattern using Room.

- **UI Layer**: 100% Jetpack Compose. ViewModels expose state via `StateFlow` and handle user actions. `Type-Safe Navigation Compose` is used for routing.
- **Domain Layer**: Contains `UseCases` and pure Kotlin models. It abstracts the data sources from the UI.
- **Data Layer**:
    - **Repository**: Mediates between Remote (Ktor) and Local (Room).
    - **Offline Strategy**: The UI observes the database (`Flow<List<Photo>>`). Network calls (Search/LoadMore) fetch data and update the database. The Single Source of Truth ensures that offline data is always available if previously cached.
    - **Ktor**: Used for Type-Safe HTTP requests.
    - **Room**: Persists photos for offline access.

## Libraries Used

- **Jetpack Compose**: UI Toolkit.
- **Hilt**: Dependency Injection.
- **Ktor Client**: Networking.
- **Kotlin Serialization**: JSON Parsing.
- **Room**: Local Database.
- **Coil**: Image Loading.
- **Coroutines & Flow**: Concurrency and Reactive streams.

## Known Issues & Trade-offs

- **Pagination**: Implemented using a simple "load more when scrolled near end" strategy. It appends to the database. Ideally, `Paging3` could be used for more robust list handling, but the custom implementation provides sufficient behavior for this scope.
- **Offline Indicator**: Connectivity is inferred from network errors. A specific global "Offline" banner could be added with `ConnectivityManager` observation.
- **Zoom**: Implemented using basic `detectTransformGestures`. High-end implementation would use a specialized library or more complex gesture handling for fling/snap-back.
