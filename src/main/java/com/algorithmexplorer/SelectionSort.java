package com.algorithmexplorer;

public class SelectionSort {

    public static BubbleSort.Result sort(int[] input) {
        if (input == null || input.length < 1 || input.length > 50) {
            throw new IllegalArgumentException(
                "Enter between 1 and 50 numbers."
            );
        }

        int[] numbers = new int[input.length];

        for (int i = 0; i < input.length; i++) {
            numbers[i] = input[i];
        }

        /*
         * Record:
         * 1 initial step
         * n(n - 1)/2 comparisons
         * n - 1 placement steps
         * 1 final step
         */
        int maximumSteps =
            numbers.length * (numbers.length - 1) / 2
            + numbers.length + 1;

        BubbleSort.Step[] steps =
            new BubbleSort.Step[maximumSteps];

        int stepCount = 0;
        long comparisons = 0;
        long swaps = 0;

        steps[stepCount++] = new BubbleSort.Step(
            numbers, -1, -1,
            "Initial array. We will find the smallest remaining value.",
            comparisons, swaps
        );

        long startTime = System.nanoTime();

        for (int position = 0;
                position < numbers.length - 1;
                position++) {

            int minimumIndex = position;

            for (int candidate = position + 1;
                    candidate < numbers.length;
                    candidate++) {

                int previousMinimumIndex = minimumIndex;
                int candidateValue = numbers[candidate];
                int minimumValue = numbers[minimumIndex];

                comparisons++;

                boolean smaller = candidateValue < minimumValue;

                if (smaller) {
                    minimumIndex = candidate;
                }

                String description =
                    "Pass " + (position + 1) +
                    ": compare candidate " + candidateValue +
                    " at index " + candidate +
                    " with current minimum " + minimumValue +
                    " at index " + previousMinimumIndex + ". ";

                if (smaller) {
                    description +=
                        "New minimum: " + candidateValue +
                        " at index " + minimumIndex + ".";
                } else {
                    description += "Keep the current minimum.";
                }

                steps[stepCount++] = new BubbleSort.Step(
                    numbers,
                    previousMinimumIndex,
                    candidate,
                    description,
                    comparisons,
                    swaps
                );
            }

            String placementDescription;

            if (minimumIndex != position) {
                int chosenValue = numbers[minimumIndex];
                int displacedValue = numbers[position];

                int temporary = numbers[position];
                numbers[position] = numbers[minimumIndex];
                numbers[minimumIndex] = temporary;

                swaps++;

                placementDescription =
                    "Place minimum " + chosenValue +
                    " at index " + position +
                    " by swapping it with " + displacedValue +
                    ". Index " + position + " is now sorted.";
            } else {
                placementDescription =
                    "The minimum is already at index " + position +
                    ". No swap is needed. This position is now sorted.";
            }

            steps[stepCount++] = new BubbleSort.Step(
                numbers,
                position,
                minimumIndex,
                placementDescription,
                comparisons,
                swaps
            );
        }

        long elapsedNanoseconds = System.nanoTime() - startTime;

        steps[stepCount++] = new BubbleSort.Step(
            numbers, -1, -1,
            "Complete. All numbers are in ascending order.",
            comparisons, swaps
        );

        return new BubbleSort.Result(
            numbers,
            steps,
            stepCount,
            comparisons,
            swaps,
            elapsedNanoseconds
        );
    }
}