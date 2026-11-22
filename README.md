# Steckbrücke for PICO

Steckbrücke is a short spatial assembly trainer. Pick either support or the crossbeam directly on the workbench, then tap a socket. The mint ring marks the next place to work. The left support, right support and crossbeam fit in that order. A wrong pair clears the selection and keeps the current guide. The panel offers the same part and socket actions without requiring precise 3D pointing.

## Local port

The PICO OS 6 client uses a volumetric PICO window, SpatialUI and the Spatial SDK 6.2.8. It loads the original Blender-derived bridge and placement ring from packaged GLB assets, adds collision and interaction components to the workpieces, and moves the three parts to their authored positions. The assembly sequence is independent of rendering. The app has no account, network or analytics functionality of its own.

The geometry and step-by-step method come from Stefan Ambrosini’s [SPECS Lens](https://devpost.com/software/steckbruecke-spatial-assembly-guide). The Kotlin app, scene rig, PICO tap path and accessible panel interactions are new. No Snap code, template dependencies or proprietary fonts are included. PICO API usage follows the [SpatialView](https://developer.picoxr.com/spatial-api/6.2.8/spatialui/foundation/com.pico.spatial.ui.foundation.content/-spatial-view.html), [Spatial Tap Gesture](https://developer.picoxr.com/spatial-api/6.2.8/spatialui/foundation/com.pico.spatial.ui.foundation.gesture/detect-spatial-tap-gesture.html) and [official Kotlin sample index](https://github.com/Pico-Developer/PICO-Spatial-SDK-Samples).

## Build with the approved toolchain

Use JDK 17, Gradle 8.10.2, Android SDK Platform 35 and Build Tools 35.0.0, following [PICO’s Android/Kotlin setup](https://picospaceprogram.com/setup/native-kotlin.md). Configure `JAVA_HOME` and `ANDROID_HOME`, or set `sdk.dir` in a local Android Gradle configuration. This source uses the same PICO repository and SDK version as the official Welcome Space sample. With the Android SDK path configured, run `gradle --no-daemon :app:assembleDebug` from this directory. The debug APK is created at `app/build/outputs/apk/debug/app-debug.apk`. Build and binary evidence are recorded in [VERIFICATION.md](VERIFICATION.md).

The standalone game logic can also be checked with Kotlin 2.0 and Java 11 or newer:

```text
kotlinc app/src/main/java/ch/spinnennetz/steckbruecke/Assembly.kt tests/AssemblyChecks.kt -jvm-target 11 -include-runtime -d assembly-checks.jar
java -jar assembly-checks.jar
```

## Device acceptance

1. Start the official PICO emulator or connect a contest-supported PICO OS 6 device with developer debugging enabled. Confirm the device ID with `adb devices -l`, then install the intended APK with `adb -s DEVICE_SERIAL install -r PATH_TO_APK`. Launch **Steckbrücke**. The bridge base, three loose parts and three sockets must be visible in the volumetric workspace.
2. Tap the left support and the right socket using spatial input. Verify that no part moves, the first ring stays active, and the status announces the next action.
3. Assemble all three parts using their highlighted sockets. Verify fixed positions, completion text and disappearance of the socket markers.
4. Reset. Repeat one correct placement and one wrong placement through the alternative panel controls.
5. Record the successful native walkthrough for the judges, including the wrong selection, the completed bridge and reset.

The existing [SPECS simulator video](https://www.youtube.com/watch?v=oU-no4Ms16U) shows the earlier Lens. A fresh PICO run and recording are required. The Acer has an AMD Radeon 860M. The [official Windows emulator guidance](https://developer.picoxr.com/document/spatial-toolkit/install-pico-emulator/) lists an NVIDIA GeForce GTX 1060 with at least 8 GB of VRAM, and no eligible runtime target was verified on this Acer.
