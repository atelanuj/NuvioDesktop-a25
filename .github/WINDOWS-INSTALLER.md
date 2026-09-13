# Build your Windows installer on GitHub

1. Push `.github/workflows/windows-installer.yml` to the repository's default
   branch (`Dev` in this fork). The source branch you select must contain the
   autoplay trailer changes.
2. Open **Actions → Build Windows Installer → Run workflow**.
3. Select `Dev` (or another branch containing the workflow and your changes),
   then click **Run workflow**.
4. When the run succeeds, open it and download **Nuvio-Windows-x64** under
   **Artifacts**. Extract the ZIP and run the `.msi` on Windows.

If Actions are disabled for the fork, enable them in the Actions tab first.
The workflow must exist on the default branch before GitHub displays the manual
Run workflow button. GitHub sign-in is needed to download artifacts. Artifacts
expire after 14 days; rerun the workflow to create a new one.

The job uses a GitHub-hosted Windows 2022 runner with Java 17, Visual Studio C++,
CMake, Android SDK and WiX. It downloads WebView2 and the Git LFS media runtime,
builds the x64 inline trailer player, runs the trailer-source tests, and builds
an unminified MSI. It uploads an artifact without publishing a GitHub release.
No tools need to be installed on your PC to build this way.

No secrets are required to compile. For account/service integrations, optionally
set the repository Actions secret `NUVIO_DESKTOP_LOCAL_PROPERTIES_BASE64` to a
base64-encoded copy of your own `local.properties` configuration. Without it,
service credentials compile as empty defaults; integrations that require those
credentials will not work. Sentry publishing and release signing are not required.
Do not commit `local.properties` to Git.

This installer is unsigned. The workflow records the commit and SHA-256 checksum
alongside the MSI. Compilation and automated tests do not verify visual playback;
check Home/detail autoplay and the audio toggle after installation.

[Manual workflow documentation](https://docs.github.com/en/actions/how-tos/manage-workflow-runs/manually-run-a-workflow)
