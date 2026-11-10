# Algorithm Explorer

Design and Analysis of Algorithms Group Project  
Uganda Christian University

Algorithm Explorer is a JavaFX desktop application that demonstrates sorting,
recursion and custom data structures. Users enter inputs, observe execution
steps, and explore operation counts, execution times and algorithm complexity.

## Group Members

| Name | Registration Number |
|---|---|
| Okiror Erasmus | S25B23/088 |
| Kyobutungi Tereza | S25B23/111 |
| Nyamwenge Bernadette | S23B23/049 |
| Aruho Bob | S24B23/115 |
| Atuhaire Sheenah | S25B23/072 |

## Install on Windows

Download the Windows installer from the
[Releases page](https://github.com/eram-ok/algorithm-explorer/releases/latest).

Open the release's Assets section, download the `.exe` installer,
run it, and launch Algorithm Explorer from the Start menu.

Java and Maven are bundled or unnecessary for end users.
The installer includes its own Java runtime.

Tested on Windows 11 x64.

## Features

- **Sorting Lab:** Bubble, selection, insertion and merge sort.
- **Recursion Lab:** Factorial, Fibonacci and recursive binary search.
- **Stack Explorer:** Custom array-based stack with Push, Pop and Peek.
- **Queue Explorer:** Custom circular queue with Enqueue, Dequeue, Front and Rear.
- **Linked List Explorer:** Custom singly linked list with insertion, search and deletion.
- Step-by-step playback for sorting and recursion.
- Operation counts, execution times and complexity explanations.
- Input validation and handling of empty or full structures.
- Laboratory state retained when navigating between screens.
- Screen-aware window sizing and maximised startup.

Session data is stored in memory and cleared when the application closes.

## Requirements

- JDK 25
- Apache Maven
- A graphical desktop environment
- Internet access for the initial Maven dependency downloads

Check your installed versions:

```powershell
java --version
javac -version
mvn -version
```

Maven should report that it is using JDK 25.

## Running the Application

Open a terminal in the project folder containing `pom.xml`, then run:

```powershell
mvn clean javafx:run
```

The first run may take longer while Maven downloads dependencies.

## Example Inputs

| Laboratory | Input or actions | Expected result |
|---|---|---|
| Sorting | `8, 3, 5, 1, 9` | `1, 3, 5, 8, 9` |
| Factorial | `5` | `120` |
| Fibonacci | `10` | `55` |
| Binary search | Array `1, 3, 5, 8, 9`; target `8` | Index `3` |
| Stack | Push `10`, push `20`, then Pop | Removes `20` |
| Queue | Enqueue `10`, enqueue `20`, then Dequeue | Removes `10` |
| Linked list | Insert Last `10`, `20`, `30`; delete `20` | `10 → 30` |

Binary search requires an ascending sorted array. Indices start at zero.

## Implementation

Sorting algorithms and the underlying stack, queue and linked list are
implemented manually. JavaFX provides the graphical controls and layouts.

Each laboratory explains which operations its counters measure.
These counters do not represent every instruction executed by the computer.

Execution time is measured using `System.nanoTime()`. Sorting and recursion
include execution-trace recording. Playback delays are separate from execution
time. Measurement scopes differ between laboratories, so the displayed times
should not be treated as directly comparable benchmarks.

Complexity explanations describe the underlying algorithms separately from
the overhead of recording and displaying execution steps.

## Building and Testing

Build the project:

```powershell
mvn package
```

Run the custom regression checks on Windows:

```powershell
mvn test-compile
java -cp "target/classes;target/test-classes" com.algorithmexplorer.AlgorithmRegressionChecks
```

On Linux or macOS, replace the classpath semicolon with a colon.

The regression runner is a plain Java main class. Running `mvn test` alone
does not execute it.

## Project Structure

- `src/main/java/com/algorithmexplorer/` — application code and algorithms.
- `src/main/resources/com/algorithmexplorer/` — interface resources and styling.
- `src/test/java/com/algorithmexplorer/` — regression checks.
- `pom.xml` — Maven configuration.
- `docs/` — project report.
- `screenshots/` — application screenshots, where included.
- `target/` — generated build files; excluded from version control.

## Project Report

[Read the full project report](docs/algorithm_explorer_documentation.pdf)

The report covers the application design, algorithms, data structures,
complexity analysis, measurement methods and testing evidence.

## Limitations

- Input sizes are limited to keep visualisations manageable.
- Stack and queue demonstrations use fixed capacities.
- Execution traces introduce additional processing and memory overhead.
- Session data is not saved after closing the application.
- Execution times vary between runs.
- The application runs through Maven; no standalone installer is included.