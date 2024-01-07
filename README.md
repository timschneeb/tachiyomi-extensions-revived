# ![app icon](./.github/readme-images/app-icon.png)Tachiyomi Extensions Revived

This repository contains several source extensions that have been removed from the official repository.

I'll probably keep maintaining the MangaDex extension here and push fixes if it breaks at some point in future.

Other sources are likely not going to be maintained actively and are stored here for archival purposes instead. Feel free to send pull requests for those, though.

This repo is a flattened & detached fork because the git history of the upstream repo is over 400 megabytes big.

# Usage

Extension sources can be downloaded, installed, and uninstalled via the main Tachiyomi app. They are installed and uninstalled like regular apps, in `.apk` format.

Google Play Protect may show a warning screen mentioning that the app is published by an unknown developer. This is because I had to use my own freshly created signing key that is not yet recognized by Google. This warning screen should stop showing up after my signing keys have built up enough reputation.

> [!IMPORTANT]
> Tachiyomi does not enable unofficial extension apps by default.
> **You must manually press the 'TRUST' button** after installing the extension on Tachiyomi's extension management screen!


## Downloads

### Add this repo to Tachiyomi Preview (r6396 and later)

Starting with the latest [Tachiyomi **preview** build](https://github.com/tachiyomiorg/tachiyomi-preview/releases) (r6396 and later), you can now add external extension repos without having to re-approve unofficial extensions after each app launch!

 
1. Update to the latest version of [Tachiyomi Preview](https://github.com/tachiyomiorg/tachiyomi-preview/releases)
2. Go to Settings > Browse > Extension repos > Add
3. Enter the following URL and accept: `https://raw.githubusercontent.com/ThePBone/tachiyomi-extensions-revived/repo/index.min.json`
4. Go to the extension management screen, refresh it, and you can now download the removed extensions.
5. After installing an extension, you need to approve it by tapping on the 'Trust' button.

> [!NOTE]
> Unofficial extensions must be manually re-approved on Tachiyomi's extension management screen after installing an update for an extension!
>
> If Tachiyomi suddenly complains about a missing source, then there probably was an extension update that needs to be approved.

### Direct APK downloads
You can also directly download the APK files in this GitHub repository in the [`repo` branch](https://github.com/ThePBone/tachiyomi-extensions-revived/tree/repo/apk).

After installing any unofficial extension, you must **manually** enable the extension in Tachiyomi.

## Disclaimer

The developer of this application does not have any affiliation with the content providers available.


## License

    Copyright 2015 Javier Tomás

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
