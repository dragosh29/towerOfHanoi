# Tower of Hanoi (Android App)

This project is a native Android application that implements the classic Tower of Hanoi puzzle game. It provides an interactive experience for users to solve the puzzle, visualize the automated solution, and track their personal bests.

## Features

- Interactive game play with configurable number of disks (3 to 10)
- Automated solution visualizer with animation
- Persistent storage of user records using SQLite
- Displays best time and least moves for each level
- Embedded WebView for learning about the Tower of Hanoi
- Custom UI with colored disks and animated feedback

## Activities Overview

| Activity                 | Description                                      |
|--------------------------|--------------------------------------------------|
| `MainActivity`           | Home screen with navigation to game modes        |
| `PlayGameActivity`       | Allows users to start a game with custom settings |
| `TowerOfHanoiActivity`   | Main gameplay screen for manual play             |
| `DisplaySolutionActivity`| Animates the optimal solution                    |
| `RecordsActivity`        | Displays and manages player records              |
| `WikiActivity`           | Opens Wikipedia article inside the app           |

## Project Structure

```
dragosh29-towerofhanoi/
└── app/
    ├── .gitignore
    └── src/
        ├── main/
        │   ├── java/com/example/towersofhanoi/
        │   │   ├── [Activities and Logic Classes]
        │   ├── res/
        │   │   ├── layout/
        │   │   ├── drawable/
        │   │   ├── values/
        │   │   └── mipmap/
        │   └── AndroidManifest.xml
        ├── test/
        └── androidTest/
```

## Database Design

The app uses a local SQLite database to store player statistics, including:

- Level (number of disks)
- Best time (in mm:ss format)
- Lowest move count
- Date when the record was set

`DBHelper` and `LevelDataSource` manage database creation and updates.

## Solution Engine

The `TowerOfHanoiSolver` class computes the recursive solution steps, which are used for the visual animation in the solution viewer activity.

## Requirements

- Android Studio Bumblebee or later
- Minimum SDK: API 21
- Target SDK: API 33+

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/dragosh29-towerofhanoi.git
   ```

2. Open the project in Android Studio.

3. Build and run the app on an emulator or physical device.

## License

This project is licensed under the MIT License.
