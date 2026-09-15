# 🗺️ Map Coloring Game

An interactive Java-based map coloring game developed as an academic **Design and Analysis of Algorithms (DAA)** project.

The project demonstrates graph-based map representation, constraint checking, backtracking, divide-and-conquer, stacks, hashing, and sorting through an interactive Human vs Computer game.

---

## 🎯 Objective

The objective of the game is to color all regions of the map while ensuring that **adjacent regions never have the same color**.

The game supports both human interaction and computer-controlled moves, along with an automated backtracking solver for finding a valid coloring.

---

## ✨ Features

- 🎮 Human vs Computer gameplay
- 🗺️ Dynamically generated map with randomized region shapes
- 🎨 Configurable number of colors
- 📐 Configurable map size
- 🤖 Computer-controlled region selection and coloring
- 🧩 Graph-based region adjacency representation
- 🔍 Automated map solving using backtracking
- 💾 Failed-state caching during backtracking
- ↩️ Undo and Redo functionality
- 💡 Interactive color hints
- 🔢 Region numbering option
- ⚠️ Invalid move and rule-violation tracking
- 📊 Real-time performance statistics
- ⏱️ Human, Computer, and Solver execution-time tracking
- 🏆 Final score and game-result summary

---

## 🧠 Algorithms & Data Structures

### 1. Graph Representation

The map is represented as a graph where:

- Each region is a **vertex**
- An edge connects two regions if they are adjacent

An adjacency-list representation is implemented using Java's `HashMap` and `HashSet`.

    Region → Adjacent Regions

This representation is used for checking color conflicts and validating the map.

---

### 2. Backtracking

The automated solver uses **backtracking** to find a valid coloring.

For each uncolored region, the solver:

1. Selects a color.
2. Checks whether the color conflicts with an adjacent region.
3. Assigns the color if it is valid.
4. Recursively processes the next region.
5. Backtracks when no valid color remains.

The number of backtracking steps is also tracked during the solving process.

---

### 3. Failed-State Caching

A `HashSet` is used to store previously failed coloring states.

Before exploring a state, the solver checks whether it has already been identified as unsuccessful. This prevents repeated exploration of the same failed configuration.

---

### 4. Divide and Conquer

The computer player selects a candidate region using a **divide-and-conquer approach**.

The candidate regions are recursively divided into smaller groups, and the results are combined by comparing the number of already-colored neighboring regions.

The region with the higher number of colored neighbors is selected.

---

### 5. Stack-Based Undo / Redo

Two stacks are used to implement:

- **Undo**
- **Redo**

The stacks maintain previous and reverted game moves, allowing the player to navigate backward and forward through gameplay actions.

Each move stores information such as the region ID, previous color, new color, and whether the move was made by the computer.

---

### 6. Insertion Sort

Rule violations are collected during gameplay.

At the end of the game, violation types are sorted by frequency using **Insertion Sort** to generate the violation summary.

---

### 7. Linear Search

Mouse interaction uses linear search through the region collection to determine which polygon contains the selected point.

---

## 🎮 Game Controls

| Control | Description |
|---|---|
| **New Game** | Generates a new map and resets the game |
| **Solve** | Automatically solves the map using backtracking |
| **Clear** | Removes colors from non-fixed regions |
| **Undo** | Reverts a previous move |
| **Redo** | Reapplies an undone move |
| **Region Numbers** | Shows or hides region IDs |
| **Left Mouse Button** | Used for normal gameplay interaction |
| **Right Mouse Button** | Used for color hints |

---

## 📊 Performance Tracking

The application records and displays several gameplay and algorithmic statistics, including:

- Human moves
- Computer moves
- Solve attempts
- Backtracking steps
- Undo operations
- Redo operations
- Average human move time
- Average computer move time
- Average solver execution time
- Number of colored regions
- Overall game progress
- Rule violations

These statistics provide a simple way to observe the behavior and performance of the algorithms during gameplay.

---

## 🗺️ Map Generation

The game dynamically generates the map based on the selected number of rows and columns.

Internal grid points are slightly randomized to create irregular polygon-shaped regions while maintaining the underlying grid-based adjacency structure.

The number of rows can be configured between **4 and 10**, while the number of colors can be configured between **3 and 10**.

---

## 🛠️ Technologies Used

- **Java**
- **Java Swing**
- **Java AWT**
- Object-Oriented Programming
- Graph Algorithms
- Backtracking
- Divide and Conquer
- HashMap
- HashSet
- Stack
- ArrayList
- Insertion Sort

---

## 📁 Project Structure

    Map-Coloring-Game/
    │
    ├── src/
    │   └── daa3/
    │       ├── GameState.java
    │       ├── MapPanel.java
    │       ├── MapPuzzleGame.java
    │       └── Region.java
    │
    ├── .gitignore
    └── README.md

### Main Classes

#### `MapPuzzleGame.java`

Handles the main game window, game flow, user controls, computer moves, scoring, performance statistics, undo/redo operations, and game completion.

#### `GameState.java`

Manages the regions, graph adjacency structure, color validation, map state, and backtracking solver.

#### `MapPanel.java`

Handles map rendering, mouse interaction, human moves, and color hints.

#### `Region.java`

Represents an individual map region, including its ID, color, fixed state, polygon, and possible-color information.

---

## ▶️ How to Run

### Requirements

- Java Development Kit (JDK)
- Eclipse IDE or another Java IDE

### Using Eclipse

1. Clone or download this repository.
2. Open Eclipse.
3. Import the project as an existing Java project.
4. Make sure the `src` directory is configured as a source folder.
5. Run `MapPuzzleGame.java`.
6. Enter the desired:
   - Number of rows
   - Number of columns
   - Number of colors
7. Start the game.

---

## 🎓 Academic Context

This project was developed as part of a **Design and Analysis of Algorithms (DAA)** academic project.

It demonstrates the practical application of algorithmic techniques and data structures in an interactive problem-solving environment.

---

## 🚀 Future Improvements

Potential future extensions include:

- More advanced graph-coloring heuristics
- Improved computer-player strategies
- Additional difficulty levels
- More sophisticated map generation
- Algorithm comparison and visualization
- Persistent game statistics
- Improved UI and animations

