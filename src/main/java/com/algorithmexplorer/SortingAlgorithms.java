package com.algorithmexplorer;

public class SortingAlgorithms {

    public static SortTrace run(String algorithm, int[] input) {
        if (input == null || input.length < 1 || input.length > 50) {
            throw new IllegalArgumentException(
                "Enter between 1 and 50 numbers."
            );
        }

        if (!"Bubble Sort".equals(algorithm)
                && !"Selection Sort".equals(algorithm)
                && !"Insertion Sort".equals(algorithm)
                && !"Merge Sort".equals(algorithm)) {
            throw new IllegalArgumentException("Choose a valid algorithm.");
        }

        int[] numbers = SortTrace.copy(input);
        SortTrace trace = new SortTrace(numbers.length);

        // Allocate the merge buffer before timing starts.
        int[] temporary = "Merge Sort".equals(algorithm)
            ? new int[numbers.length]
            : null;

        trace.record(numbers, "Initial array.", -1, -1, null);

        long start = System.nanoTime();

        switch (algorithm) {
            case "Bubble Sort":
                bubbleSort(numbers, trace);
                break;

            case "Selection Sort":
                selectionSort(numbers, trace);
                break;

            case "Insertion Sort":
                insertionSort(numbers, trace);
                break;

            case "Merge Sort":
                mergeSort(
                    numbers, temporary, 0, numbers.length - 1, trace
                );
                break;

            default:
                throw new IllegalArgumentException("Unknown algorithm.");
        }

        trace.setElapsedNanoseconds(System.nanoTime() - start);

        trace.record(
            numbers,
            "Complete. All numbers are in ascending order.",
            -1, -1, null
        );

        return trace;
    }

    private static void bubbleSort(int[] numbers, SortTrace trace) {
        for (int pass = 0; pass < numbers.length - 1; pass++) {
            boolean swapped = false;

            for (int j = 0; j < numbers.length - 1 - pass; j++) {
                int left = numbers[j];
                int right = numbers[j + 1];

                trace.countComparison();

                String explanation =
                    "Pass " + (pass + 1) +
                    ": compare " + left + " and " + right + ". ";

                if (left > right) {
                    int temporary = numbers[j];
                    numbers[j] = numbers[j + 1];
                    numbers[j + 1] = temporary;

                    trace.countSwap();
                    swapped = true;
                    explanation += "Swap them.";
                } else {
                    explanation += "No swap needed.";
                }

                trace.record(
                    numbers, explanation, j, j + 1, null
                );
            }

            if (!swapped) {
                break;
            }
        }
    }

    private static void selectionSort(int[] numbers, SortTrace trace) {
        for (int position = 0;
                position < numbers.length - 1;
                position++) {

            int minimum = position;

            for (int candidate = position + 1;
                    candidate < numbers.length;
                    candidate++) {

                int oldMinimum = minimum;
                trace.countComparison();

                String explanation =
                    "Compare candidate " + numbers[candidate] +
                    " with current minimum " + numbers[minimum] + ". ";

                if (numbers[candidate] < numbers[minimum]) {
                    minimum = candidate;
                    explanation += "The candidate is the new minimum.";
                } else {
                    explanation += "Keep the current minimum.";
                }

                trace.record(
                    numbers, explanation, oldMinimum, candidate, null
                );
            }

            String explanation;

            if (minimum != position) {
                int temporary = numbers[position];
                numbers[position] = numbers[minimum];
                numbers[minimum] = temporary;

                trace.countSwap();

                explanation =
                    "Swap the minimum into index " + position +
                    ". This position is now sorted.";
            } else {
                explanation =
                    "The minimum is already at index " + position +
                    ". No swap is needed.";
            }

            trace.record(
                numbers, explanation, position, minimum, null
            );
        }
    }

    private static void insertionSort(int[] numbers, SortTrace trace) {
        for (int i = 1; i < numbers.length; i++) {
            int key = numbers[i];
            int j = i - 1;

            trace.record(
                numbers,
                "Hold " + key + " as the key. Insert it into the " +
                "sorted portion to its left.",
                i, -1, key
            );

            while (j >= 0) {
                trace.countComparison();
                boolean move = numbers[j] > key;

                trace.record(
                    numbers,
                    "Compare " + numbers[j] + " with held key " + key +
                    (move
                        ? ". This value is larger, so shift it right."
                        : ". It is not larger, so stop searching left."),
                    j, -1, key
                );

                if (!move) {
                    break;
                }

                int shiftedValue = numbers[j];
                numbers[j + 1] = numbers[j];
                trace.countWrite();

                trace.record(
                    numbers,
                    "Shift " + shiftedValue + " from index " + j +
                    " to index " + (j + 1) +
                    ". The key is still held separately.",
                    j, j + 1, key
                );

                j--;
            }

            numbers[j + 1] = key;
            trace.countWrite();

            trace.record(
                numbers,
                "Write key " + key + " into index " + (j + 1) +
                ". Indexes 0 through " + i + " are now sorted.",
                j + 1, -1, null
            );
        }
    }

    private static void mergeSort(
            int[] numbers,
            int[] temporary,
            int left,
            int right,
            SortTrace trace) {

        // Base case: one element is already sorted.
        if (left >= right) {
            return;
        }

        int middle = left + (right - left) / 2;

        trace.record(
            numbers,
            "Split indexes " + left + ".." + right +
            " into " + left + ".." + middle +
            " and " + (middle + 1) + ".." + right + ".",
            left, right, null
        );

        mergeSort(numbers, temporary, left, middle, trace);
        mergeSort(numbers, temporary, middle + 1, right, trace);

        merge(numbers, temporary, left, middle, right, trace);
    }

    private static void merge(
            int[] numbers,
            int[] temporary,
            int left,
            int middle,
            int right,
            SortTrace trace) {

        int first = left;
        int second = middle + 1;
        int destination = left;

        while (first <= middle && second <= right) {
            trace.countComparison();

            int chosenIndex;

            // Choose the left value on equality to preserve stability.
            if (numbers[first] <= numbers[second]) {
                chosenIndex = first;
            } else {
                chosenIndex = second;
            }

            trace.record(
                numbers,
                "Merge indexes " + left + ".." + right +
                ": compare " + numbers[first] +
                " and " + numbers[second] +
                ". Choose " + numbers[chosenIndex] +
                (numbers[first] == numbers[second]
                    ? " from the left half because the values are equal."
                    : "."),
                first, second, null
            );

            temporary[destination] = numbers[chosenIndex];
            trace.countWrite();

            trace.record(
                numbers,
                "Write " + numbers[chosenIndex] +
                " into temporary[" + destination + "]. " +
                "Temporary segment so far: " +
                formatRange(temporary, left, destination) +
                ". Main-array boxes are unchanged.",
                chosenIndex, -1, null
            );

            destination++;

            if (chosenIndex == first) {
                first++;
            } else {
                second++;
            }
        }

        while (first <= middle) {
            temporary[destination] = numbers[first];
            trace.countWrite();

            trace.record(
                numbers,
                "Right half exhausted. Copy " + numbers[first] +
                " into temporary[" + destination + "]. " +
                "Temporary segment so far: " +
                formatRange(temporary, left, destination) +
                ". No value comparison is needed.",
                first, -1, null
            );

            first++;
            destination++;
        }

        while (second <= right) {
            temporary[destination] = numbers[second];
            trace.countWrite();

            trace.record(
                numbers,
                "Left half exhausted. Copy " + numbers[second] +
                " into temporary[" + destination + "]. " +
                "Temporary segment so far: " +
                formatRange(temporary, left, destination) +
                ". No value comparison is needed.",
                second, -1, null
            );

            second++;
            destination++;
        }

        for (int index = left; index <= right; index++) {
            numbers[index] = temporary[index];
            trace.countWrite();

            trace.record(
                numbers,
                "Copy temporary[" + index + "] = " + temporary[index] +
                " back into main-array index " + index +
                ". The merged segment is " +
                formatRange(temporary, left, right) + ".",
                index, -1, null
            );
        }
    }

    private static String formatRange(int[] values, int start, int end) {
        StringBuilder text = new StringBuilder("[");

        for (int i = start; i <= end; i++) {
            if (i > start) {
                text.append(", ");
            }

            text.append(values[i]);
        }

        return text.append("]").toString();
    }
}