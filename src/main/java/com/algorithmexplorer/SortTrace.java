package com.algorithmexplorer;

public class SortTrace {

    public static class Step {
        public final int[] values;
        public final String explanation;
        public final int firstIndex;
        public final int secondIndex;
        public final Integer heldKey;
        public final long comparisons;
        public final long swaps;
        public final long writes;

        public Step(
                int[] values,
                String explanation,
                int firstIndex,
                int secondIndex,
                Integer heldKey,
                long comparisons,
                long swaps,
                long writes) {

            this.values = copy(values);
            this.explanation = explanation;
            this.firstIndex = firstIndex;
            this.secondIndex = secondIndex;
            this.heldKey = heldKey;
            this.comparisons = comparisons;
            this.swaps = swaps;
            this.writes = writes;
        }
    }

    private final Step[] steps;
    private int stepCount;

    private long comparisons;
    private long swaps;
    private long writes;
    private long elapsedNanoseconds;

    public SortTrace(int inputSize) {
        /*
         * Enough for our three algorithms with at most 50 elements.
         * Includes comparison, movement, initial and final steps.
         */
        steps = new Step[2 * inputSize * inputSize + 2];
    }

    public void record(
            int[] values,
            String explanation,
            int firstIndex,
            int secondIndex,
            Integer heldKey) {

        if (stepCount == steps.length) {
            throw new IllegalStateException("Trace capacity exceeded.");
        }

        steps[stepCount++] = new Step(
            values,
            explanation,
            firstIndex,
            secondIndex,
            heldKey,
            comparisons,
            swaps,
            writes
        );
    }

    public void countComparison() {
        comparisons++;
    }

    public void countSwap() {
        swaps++;
        writes += 2;
    }

    public void countWrite() {
        writes++;
    }

    public void setElapsedNanoseconds(long value) {
        elapsedNanoseconds = value;
    }

    public int getStepCount() {
        return stepCount;
    }

    public Step getStep(int index) {
        if (index < 0 || index >= stepCount) {
            throw new IndexOutOfBoundsException("Invalid step index.");
        }

        return steps[index];
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps;
    }

    public long getWrites() {
        return writes;
    }

    public long getElapsedNanoseconds() {
        return elapsedNanoseconds;
    }

    public static int[] copy(int[] original) {
        int[] result = new int[original.length];

        for (int i = 0; i < original.length; i++) {
            result[i] = original[i];
        }

        return result;
    }

    public static String format(int[] values) {
        StringBuilder text = new StringBuilder("[");

        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                text.append(", ");
            }

            text.append(values[i]);
        }

        return text.append("]").toString();
    }
}