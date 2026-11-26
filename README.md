# MiniBank

A small Android banking app built with **Jetpack Compose**, featuring a login screen with **biometric authentication** (fingerprint / face).

> 🚧 Work in progress. Login is currently mocked. There is no real backend yet.

<!-- Add a screenshot: save it as docs/login.png and uncomment the line below -->
<!-- <img src="docs/login.png" alt="Login screen" width="300"/> -->

## Features

- Username and password login with show/hide password, loading state and error messages
- Biometric login using the system `BiometricPrompt` (Class 3 / `BIOMETRIC_STRONG`)
- Biometric prompt shown automatically on launch when available, with a password fallback
- Material 3 theming with light and dark mode

## Tech stack

| Area | Library |
|------|---------|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM (`ViewModel` + `StateFlow`) |
| Biometrics | `androidx.biometric` |
| Build | Gradle (Kotlin DSL) with a version catalog |

## Requirements

- Android Studio (latest stable)
- JDK 11+
- A device or emulator running **Android 11 (API 30)** or newer

## Getting started

```bash
git clone https://github.com/geldf/MiniBank.git
cd MiniBank
./gradlew :app:installDebug
```

Or open the project in Android Studio and press **Run**.

### Demo login

| Username | Password |
|----------|----------|
| `demo`   | `password` |

### Testing biometrics on an emulator

1. On the emulator, open **Settings → Security → Fingerprint** and add a fingerprint.
2. When asked to touch the sensor, open the emulator's **Extended controls (⋯) → Fingerprint** and click **Touch sensor**.
3. Launch MiniBank. The biometric prompt appears, and **Touch sensor** logs you in.

## Project structure

```
app/src/main/java/com/angelhia/minibank/
├── MainActivity.kt               # Entry point (FragmentActivity, required by BiometricPrompt)
├── auth/
│   └── BiometricAuthenticator.kt # Wraps BiometricManager / BiometricPrompt
└── ui/
    ├── login/
    │   ├── LoginScreen.kt        # Compose UI (stateless screen + stateful route)
    │   └── LoginViewModel.kt     # Login state and logic
    └── theme/
        └── Theme.kt              # Material 3 theme
```

## Roadmap

- [ ] Connect login to a real authentication API
- [ ] Only offer biometrics after a first password login, and store the session token in the Android Keystore, unlocked by a biometric check (`CryptoObject`)
- [ ] Home screen with account balance and transactions
- [ ] Navigation (Navigation Compose)
- [ ] Unit and UI tests
