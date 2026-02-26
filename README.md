# ShareTheImage

An offline-first image discovery application built with Jetpack Compose, Hilt, Room, Ktor and Paging3 using the Unsplash API.

## Build Instructions

1. **API Key Configuration**:
   The app uses the Unsplash API. The access key is injected via `buildConfigField`.
   You should add your API Key in `local.properties` (which is gitignored) at the root of the project:
   ```properties
   UNSPLASH_ACCESS_KEY=your_access_key_here
   ```
