# ShareTheImage

An offline-first image discovery application built with Jetpack Compose, Hilt, Room, Ktor and Paging3 using the Unsplash API.

## Build Instructions

1. **API Key Configuration**:
   The app uses the Unsplash API. The access key is injected via `buildConfigField`.
   You should add your API Key in `local.properties` (which is gitignored) at the root of the project:
   ```properties
   UNSPLASH_ACCESS_KEY=your_access_key_here
   ```

## Developer Thoughts

1. **MVI**:
   Usually I write most screens in an MVI pattern, but here it seemed too much to introduce this pattern. 
   Also my usual MVI approach seemed not very compatible with the paging3 library. 
   If you want to see, how I usually approach MVI, you can have a look at ProfileEditViewModel in the STM App.

2. **Kotlin Mulitplatform**:
   I decided to choose mostly libraries, which are also available for Kotlin Multiplatform.
   Like Ktor, Paging3 and Kotlin Serialization.
   I think the only exception is Room in the data layer.
   So the project could be easily ported to other platforms.

3. **Clean Architecture Domain Layer**:
   I decided not to follow textbook clean architecture. For example the domain layer and the ui layer share the same
   model. For the same reason i also decided to do the mapping of the paging3 models inside the domain layer PhotoListMapper. 
   For me the Viewmodel is part of the domain layer and that's also how I implemented it here.
   That's for me because domain logic usually starts in the Viewmodel and if it grows I start extracting code,
   but most times the Viewmodel and the extracted code still belong to the same testing unit for me.

4. **Android test**:
   I didn't have enough time to setup the android end to end test properly,
   but at least a basic setup is up and running.

