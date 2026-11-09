# Algorithm Explorer

CSC2107 group project built with Java and JavaFX.

The application allows users to enter data, execute manually implemented
algorithms, and inspect results, execution steps, operation counts and timing.

## Implemented so far

- JavaFX home screen and laboratory navigation
- Integer input validation
- Bubble Sort
- Selection Sort
- Insertion Sort
- Merge Sort
- Step-by-step visual playback
- Play, Pause, Previous, Next and Restart controls
- Adjustable playback speed
- Comparisons, swaps and array-write counters
- Execution timing and complexity explanations

## Planned modules

- Custom stack
- Custom circular queue
- Custom singly linked list
- Recursion laboratory

## Requirements

- JDK 25
- Apache Maven
- Internet access for the initial dependency download

## Run

From the folder containing pom.xml:

```shell
mvn clean javafx:run
```

## Implementation rules

Sorting algorithms are implemented manually without built-in sorting.
Algorithm data and recorded steps use arrays rather than ready-made
list, stack or queue implementations.

JavaFX collection APIs are used only to manage interface elements.

## Measurement method

Counted operations = value comparisons + array writes.

- Each swap contributes two array writes.
- Merge Sort includes writes to its temporary array.
- Loop checks, index updates and trace-copying operations are excluded
  from the operation count.
- Recorded execution time includes sorting, counters and internal
  step recording.
- Initial setup, the final snapshot and playback are excluded.

These instrumented timings are not pure sorting benchmarks.