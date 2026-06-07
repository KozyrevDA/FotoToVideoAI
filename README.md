# FotoToVideoAI — Photo to Video AI Generator

A Kotlin Multiplatform mobile application (Android & iOS) that transforms photos into AI-generated videos using cutting-edge models like **VEO 3** and **Kling v2/v3**.

---

## Features

- **AI Video Generation** — Convert any photo into a short video using VEO 3 (primary) with automatic fallback to Kling (Hedra API)
- **Trial Generation** — New users can try the app without registration or payment (one free generation via "Photo by Sample")
- **Multiple Templates** — Pre-built templates for quick video creation
- **Token System** — In-app currency for managing generation requests
- **Multilingual** — Supports multiple languages (Russian, English, Belarusian, and more)
- **Payments** — RuStore billing integration for token purchases
- **Authentication** — Google Sign-In and email/password login

---

## Tech Stack

| Layer | Technology |
|---|---|
| Mobile (shared) | Kotlin Multiplatform + Compose Multiplatform |
| Android | Kotlin, Jetpack Compose |
| iOS | SwiftUI + Compose Multiplatform |
| Backend | Ktor (Kotlin) |
| Database | PostgreSQL + Exposed ORM |
| AI Models | VEO 3 (laozhang.ai), Kling v3 (Hedra API) |
| Auth | Google OAuth, JWT |
| DI | Koin |
| Networking | Ktor Client |
| Image Loading | Coil |

---

## Project Structure

```
composeApp/
├── src/
│   ├── commonMain/        # Shared business logic, UI screens, network layer
│   ├── androidMain/       # Android-specific implementations
│   └── iosMain/           # iOS-specific implementations
iosApp/                    # iOS entry point (Xcode project)
```

---

## Build & Run

### Android

```shell
# macOS / Linux
./gradlew :composeApp:assembleDebug

# Windows
.\gradlew.bat :composeApp:assembleDebug
```

Or open the project in **Android Studio** and press Run.

### iOS

Open the `/iosApp` directory in **Xcode** and run on simulator or device.

---

## AI Model Architecture

```
User Request
     │
     ▼
  VEO 3 (primary)
     │
     ├── Success → Return video
     │
     └── Failure (overload / timeout)
          │
          ▼
       Kling v3 Standard (fallback via Hedra API)
          │
          ├── Success → Return video
          │
          └── Failure → Show error to user
```

---

## Requirements

- Android 7.0+ (API 24)
- iOS 16+
- Android Studio Hedgehog or newer
- Xcode 15+
