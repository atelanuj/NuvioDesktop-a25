# Nuvio build container

This container installs Temurin JDK 17, Android command-line tools/SDK 36 (needed
when Gradle configures the Android modules), CMake, C/C++ compilers, GTK,
WebKitGTK, libmpv, GStreamer, Xvfb and Linux packaging tools. Android SDK licenses
are accepted during image creation. No host Java installation is needed.

Docker Desktop must use Linux containers. Allocate at least 8 GB RAM to Docker.
Run the following from the repository root in PowerShell:

```powershell
docker compose -f compose.build.yaml up -d --build
docker compose -f compose.build.yaml exec builder nuvio-build
```

The second command compiles the desktop code and runs desktop tests under Xvfb.
The first build downloads the toolchain and Maven/Gradle dependencies and can
take several minutes. Subsequent runs reuse named volumes. Run only one build
at a time because the builder shares its workspace.

The checkout's Android targets request SDK 37, which was unavailable from Google's
stable SDK repository during setup. This container installs SDK 36 for desktop
Gradle configuration; it does not change the project's Android target or claim
to build APKs. Once SDK 37 is published, the image's `ANDROID_PLATFORM` and
`ANDROID_BUILD_TOOLS` build arguments can be updated for Android work.

Build a Linux DEB installer:

```powershell
docker compose -f compose.build.yaml exec builder nuvio-build :composeApp:packageDeb
```

Reports and packages are copied to `build/docker-artifacts/` on the host, including
`composeApp/compose/binaries/main/deb/` for the DEB. The source checkout is mounted
read-only; compilation uses a separate Linux volume. Shell line endings and the
Android SDK path are adjusted only in that copy. If present, `local.properties`
is copied at runtime for your own service configuration; it is never baked into
the Docker image. Account/Trakt/TMDB integrations may need your own configuration.
Do not distribute packages containing configuration you do not intend to share.

Inspect or stop the builder:

```powershell
docker compose -f compose.build.yaml exec builder java -version
docker compose -f compose.build.yaml exec builder bash
docker compose -f compose.build.yaml down
```

Stopping the builder retains the dependency and workspace caches.

## Windows installer

This Linux container **cannot produce a Windows MSI**. Compose packaging requires
the target OS, and this app additionally compiles Windows native player DLLs with
MSVC and WebView2. To install the autoplay changes on Windows, use a Windows host
or Windows CI runner with the prerequisites in `.github/workflows/desktop-release.yml`,
then run `./gradlew.bat :composeApp:packageReleaseMsi`. The release workflow also
requires its documented repository secrets. This container provides a repeatable
Linux compile/test/package environment, not a Windows installer.

References: [Compose packaging](https://kotlinlang.org/docs/multiplatform/compose-native-distribution.html),
[Android SDK tools](https://developer.android.com/studio#command-line-tools-only).
