# Cross-Platform Rules

This is a Kotlin Multiplatform project targeting iOS, Android, and Desktop. Every change must land on all three platforms unless the user explicitly says otherwise.

## Project layout

- `shared/src/commonMain` – shared logic and Compose Multiplatform UI (used by Android and Desktop)
- `shared/src/iosMain` – iOS-specific Kotlin glue (Koin, Flow bridging, Redux utils)
- `androidApp/` – Android entry point, manifest, WorkManager sync
- `desktopApp/` – Desktop (JVM) entry point
- `iosApp/iosApp/View/` – native SwiftUI UI for iOS (not shared with Compose)

## Rules

1. **Logic, state, data, networking, storage**: implement in `shared/src/commonMain` so all platforms get it. Do not duplicate it per platform.
2. **UI changes**: update the Compose UI in `shared/src/commonMain/.../ui/` (covers Android and Desktop) AND the matching SwiftUI view in `iosApp/iosApp/View/` (iOS). A UI change is not complete until both are updated and look and behave the same.
3. **New shared API used by iOS**: if SwiftUI needs new state or actions, expose them through the shared module (`iosMain` helpers if required) and wire them into the SwiftUI views.
4. **Platform-specific code** (`androidApp`, `desktopApp`, `iosMain`, Swift files): when a change touches one, check whether the other platforms need an equivalent change (permissions, entry points, background sync, window/navigation setup) and make it.
5. **New strings and resources**: add to `shared/src/commonMain/composeResources` and mirror them in the iOS app where iOS renders them.
6. **Dependencies**: add to `gradle/libs.versions.toml` and the relevant source sets for every target that needs them.
7. **Deliberate exceptions**: if a change is intentionally limited to one platform, say so in the summary and explain why.

## Swift (iOS) conventions

- SwiftUI only. Use `NavigationStack`, `TabView`, and built-in controls. No custom back buttons.
- PascalCase file names; every view has a `#Preview`.
- Respect safe areas; wrap long screens in `ScrollView`.
- Use `AsyncImage` for remote images and `Color` instead of asset catalog references.

## Before finishing a task

Confirm the change in the final summary for each platform: iOS, Android, Desktop.
