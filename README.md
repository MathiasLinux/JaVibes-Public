# JaVibes

## Introduction

JaVibes is a Java application that allows users to play music from their Jellyfin server. It is currently in development and is not yet ready for use. It was developed as a project for college.

## Features

- [x] Play music from Jellyfin server
- [x] Ajust volume
- [x] Play, pause, skip, previous
- [x] Settings to change the server connection
- [ ] Search for music
- [ ] Playlists
- [ ] Shuffle and repeat
- [ ] Play by Titlebill
- [ ] Play by Artist

## Requirements

- Java 21
- Jellyfin server
- Maven 3.8

## Installation

1. Clone the repository
2. Run `mvn clean javafx:run`
3. Enjoy !

Currently, the application has no uber jar, so you need to run it with maven, because at the time of development, ok http client was not working with jlink.

## Screenshots

![Home page of JaVibes](screenshots/JaVibes_home.png?raw=true "Home page of JaVibes")

![Albums page of JaVibes](screenshots/JaVibes_Albums.png?raw=true "Albums page of JaVibes")

![An Album page of JaVibes](screenshots/JaVibes_an_album.png?raw=true "An Album page of JaVibes")

![Settings page of JaVibes](screenshots/JaVibes_settings.png?raw=true "Settings page of JaVibes")

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## Thanks

Thanks to Jellyfin for their great work on their server and their API. This project would not have been possible without them.

## Final note

This project was created to discover Java and JavaFX. It is not intended to be used in production. It is a student project and may contain bugs. If you find any, please report them in the issues section.

Enjoy JaVibes !