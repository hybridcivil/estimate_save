# Building Estimate & Quantity Surveying App

A comprehensive Android application for Civil Engineers, Contractors, and Estimators built with Kotlin and Jetpack Compose.

## Features
- **Structural Estimators**: Footing, Column, Beam, Two-Way Slab, Staircase, Brickwork & Plastering.
- **Custom Items & Rates**: Live rate modification for cement, sand, stone aggregate, steel, and bricks.
- **Offline Persistence**: Full Room database support for storing multiple client projects.
- **BOQ & PDF Export**: Instant professional engineering reports generation and sharing.

---

## 🚀 Building the APK via GitHub Actions (Automated CI/CD)

The project includes a ready-to-use GitHub Actions workflow located at `.github/workflows/build-apk.yml`.

### How to Build & Download APK on GitHub:
1. Push this repository to your GitHub account (e.g. `main` or `master` branch).
2. On GitHub, navigate to the **"Actions"** tab of your repository.
3. Select the **"Build Android APK"** workflow on the left.
4. Click **"Run workflow"** (or simply push a commit to trigger it automatically).
5. Once the build succeeds, click on the completed workflow run.
6. Under the **"Artifacts"** section at the bottom, download **`BuildingEstimate-Debug-APK`**.
7. Extract the downloaded ZIP file to get your ready-to-install `.apk` file!

---

## 💻 Local Build Instructions

### Prerequisites
- JDK 21 (Temurin or OpenJDK)
- Android SDK (API Level 36)

### Build Command
To build the debug APK locally:
```bash
./gradlew assembleDebug
```
The output APK will be available in:
```
app/build/outputs/apk/debug/app-debug.apk
```
