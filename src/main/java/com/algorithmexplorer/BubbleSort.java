package com.algorithmexplorer;

public class BubbleSort {

    public static class Step {
        private final int[] values;
        private final int leftIndex;
        private final int rightIndex;
        private final String description;
        private final long comparisons;
        private final long swaps;

        public Step(
                int[] numbers,
                int leftIndex,
                int rightIndex,
                String description,
                long comparisons,
                long swaps) {

            this.values = copyArray(numbers);
            this.leftIndex = leftIndex;
            this.rightIndex = rightIndex;
            this.description = description;
            this.comparisons = comparisons;
            this.swaps = swaps;
        }

        public int[] getValues() {
            return values;
        }

        public int getLeftIndex() {
            return leftIndex;
        }

        public int getRightIndex() {
            return rightIndex;
        }

        public String getDescription() {
            return description;
        }

        public long getComparisons() {
            return comparisons;
        }

        public long getSwaps() {
            return swaps;
        }
    }

    public static class Result {
        private final int[] sortedNumbers;
        private final Step[] steps;
        private final int stepCount;
        private final long comparisons;
        private final long swaps;
        private final long elapsedNanoseconds;

        public Result(
                int[] sortedNumbers,
                Step[] steps,
                int stepCount,
                long comparisons,
                long swaps,
                long elapsedNanoseconds) {

            this.sortedNumbers = sortedNumbers;
            this.steps = steps;
            this.stepCount = stepCount;
            this.comparisons = comparisons;
            this.swaps = swaps;
            this.elapsedNanoseconds = elapsedNanoseconds;
        }

        public int[] getSortedNumbers() {
            return sortedNumbers;
        }

        public Step getStep(int index) {
            if (index < 0 || index >= stepCount) {
                throw new IndexOutOfBoundsException(
                    "Invalid step index: " + index
                );
            }

            return steps[index];
        }

        public int getStepCount() {
            return stepCount;
        }

        public long getComparisons() {
            return comparisons;
        }

        public long getSwaps() {
            return swaps;
        }

        public long getArrayWrites() {
            return swaps * 2;
        }

        public long getCountedOperations() {
            return comparisons + getArrayWrites();
        }

        public long getElapsedNanoseconds() {
            return elapsedNanoseconds;
        }
    }

    public static Result sort(int[] input) {
        if (input == null || input.length < 1 || input.length > 50) {
            throw new IllegalArgumentException(
                "Enter between 1 and 50 numbers."
            );
        }

        int[] numbers = copyArray(input);

        /*
         * At most n(n - 1)/2 comparisons.
         * Store one step per comparison, plus initial and final steps.
         * This is a plain array, not a ready-made list.
         */
        int maximumSteps =
            numbers.length * (numbers.length - 1) / 2 + 2;

        Step[] steps = new Step[maximumSteps];
        int stepCount = 0;

        long comparisons = 0;
        long swaps = 0;

        steps[stepCount++] = new Step(
            numbers, -1, -1,
            "Initial array. No comparisons have been made.",
            comparisons, swaps
        );

        /*
         * This measures the instrumented sorting loop:
         * sorting + counters + comparison-step snapshots.
         * Initial setup, final step and interface playback are excluded.
         */
        long startTime = System.nanoTime();

        for (int pass = 0; pass < numbers.length - 1; pass++) {
            boolean swappedThisPass = false;

            for (int j = 0; j < numbers.length - 1 - pass; j++) {
                int leftValue = numbers[j];
                int rightValue = numbers[j + 1];

                comparisons++;
                boolean shouldSwap = leftValue > rightValue;

                if (shouldSwap) {
                    int temporary = numbers[j];
                    numbers[j] = numbers[j + 1];
                    numbers[j + 1] = temporary;

                    swaps++;
                    swappedThisPass = true;
                }

                String description =
                    "Pass " + (pass + 1) +
                    ": compare " + leftValue + " and " + rightValue +
                    " at indexes " + j + " and " + (j + 1) + ". ";

                if (shouldSwap) {
                    description +=
                        leftValue + " > " + rightValue +
                        ", so swap them. The boxes show the result.";
                } else {
                    description +=
                        leftValue + " <= " + rightValue +
                        ", so no swap is needed.";
                }

                steps[stepCount++] = new Step(
                    numbers, j, j + 1,
                    description, comparisons, swaps
                );
            }

            if (!swappedThisPass) {
                break;
            }
        }

        long elapsedNanoseconds = System.nanoTime() - startTime;

        String finalDescription = numbers.length == 1
            ? "Complete. One number is already sorted."
            : "Complete. All numbers are in ascending order.";

        steps[stepCount++] = new Step(
            numbers, -1, -1,
            finalDescription, comparisons, swaps
        );

        return new Result(
            numbers,
            steps,
            stepCount,
            comparisons,
            swaps,
            elapsedNanoseconds
        );
    }

    private static int[] copyArray(int[] original) {
        int[] copy = new int[original.length];

        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i];
        }

        return copy;
    }

    public static String formatArray(int[] numbers) {
        StringBuilder text = new StringBuilder("[");

        for (int i = 0; i < numbers.length; i++) {
            if (i > 0) {
                text.append(", ");
            }

            text.append(numbers[i]);
        }

        text.append("]");
        return text.toString();
    }
}