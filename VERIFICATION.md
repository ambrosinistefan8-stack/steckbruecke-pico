# Steckbrücke build evidence

Local build recorded 22 November 2026 on the Acer. The debug APK is an ARM64 build for a supported PICO OS 6 test target.

| Check | Recorded result |
|---|---|
| Host build | `gradle --no-daemon :app:assembleDebug` succeeded. All 34 Gradle tasks completed. |
| Toolchain | Temurin JDK 17.0.21.1, Gradle 8.10.2, Android Gradle Plugin 8.8.0, Kotlin 2.0.0, Android Platform 35, Build Tools 35.0.0 and PICO Spatial SDK BOM 6.2.8. |
| Standalone Kotlin rules | 27 input combinations passed, including missing selection, wrong part or socket and all accepted transitions. Completion no-ops and clean reset also passed. |
| Package | `aapt2` read application ID `ch.spinnennetz.steckbruecke`, version 0.1.0, launcher `LaunchActivity`, API level 35 and `arm64-v8a`. The compiled manifest retained the volumetric PICO window metadata. |
| Package integrity | ZIP CRC passed. Both original GLB files were included byte-for-byte and without compression. PICO SpatialView bytecode and the compiled scene were present. |
| Signature | Android’s `apksigner` confirmed a valid v2 debug signature. |

Debug APK size is 30,724,184 bytes. SHA-256 is `ff1fc0c4ea5c0db858d4cd99f0fdc8685e5a5d818d17585d33676724889809e0`.

The checks establish compilation and packaging and exercise the assembly logic on the JVM. Installation, launch, rendering, spatial taps and native recording on a PICO device or its official emulator are pending.
