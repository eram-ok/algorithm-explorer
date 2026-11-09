package com.algorithmexplorer;

public class RecursionAlgorithms {

    public static class Step {
        public final String description;
        public final String activeStack;
        public final long calls;
        public final long baseChecks;
        public final long multiplications;
        public final long additions;
        public final long valueComparisons;
        public final int activeDepth;

        public Step(
                String description,
                String activeStack,
                long calls,
                long baseChecks,
                long multiplications,
                long additions,
                long valueComparisons,
                int activeDepth) {

            this.description = description;
            this.activeStack = activeStack;
            this.calls = calls;
            this.baseChecks = baseChecks;
            this.multiplications = multiplications;
            this.additions = additions;
            this.valueComparisons = valueComparisons;
            this.activeDepth = activeDepth;
        }
    }

    public static class Result {
        private final Step[] steps;
        private int stepCount;

        // Display-only model of the active recursive calls.
        private final String[] activeCalls;
        private int activeSize;

        private long value;
        private long calls;
        private long baseChecks;
        private long multiplications;
        private long additions;
        private long valueComparisons;
        private int maximumDepth;
        private long elapsedNanoseconds;

        private Result(int stepCapacity, int stackCapacity) {
            steps = new Step[stepCapacity];
            activeCalls = new String[stackCapacity];
        }

        private void enter(String call) {
            calls++;
            activeCalls[activeSize++] = call;

            if (activeSize > maximumDepth) {
                maximumDepth = activeSize;
            }
        }

        private void leave() {
            activeSize--;
            activeCalls[activeSize] = null;
        }

        private void record(String explanation) {
            if (stepCount == steps.length) {
                throw new IllegalStateException("Trace capacity exceeded.");
            }

            StringBuilder stack = new StringBuilder();

            if (activeSize == 0) {
                stack.append("Empty — all calls have returned.");
            } else {
                for (int i = activeSize - 1; i >= 0; i--) {
                    stack.append(
                        i == activeSize - 1 ? "TOP → " : "      "
                    );

                    stack.append(activeCalls[i]);

                    stack.append(
                        i == activeSize - 1
                            ? " — current call"
                            : " — waiting for a result"
                    );

                    stack.append("\n");
                }
            }

            steps[stepCount++] = new Step(
                explanation,
                stack.toString(),
                calls,
                baseChecks,
                multiplications,
                additions,
                valueComparisons,
                activeSize
            );
        }

        public Step getStep(int index) {
            if (index < 0 || index >= stepCount) {
                throw new IndexOutOfBoundsException("Invalid step index.");
            }

            return steps[index];
        }

        public int getStepCount() {
            return stepCount;
        }

        public long getValue() {
            return value;
        }

        public long getCalls() {
            return calls;
        }

        public long getBaseChecks() {
            return baseChecks;
        }

        public long getMultiplications() {
            return multiplications;
        }

        public long getAdditions() {
            return additions;
        }

        public long getValueComparisons() {
            return valueComparisons;
        }

        public int getMaximumDepth() {
            return maximumDepth;
        }

        public long getElapsedNanoseconds() {
            return elapsedNanoseconds;
        }
    }

    public static Result run(String algorithm, int input) {
        if ("Factorial".equals(algorithm)) {
            return factorial(input);
        }

        if ("Fibonacci".equals(algorithm)) {
            return fibonacci(input);
        }

        throw new IllegalArgumentException(
            "This algorithm requires a sorted array and a target."
        );
    }

    public static Result factorial(int input) {
        if (input < 0 || input > 20) {
            throw new IllegalArgumentException(
                "Factorial accepts integers from 0 to 20."
            );
        }

        Result result = new Result(2 * (input + 1), input + 1);

        long start = System.nanoTime();
        result.value = factorialRecursive(input, result);
        result.elapsedNanoseconds = System.nanoTime() - start;

        return result;
    }

    public static Result fibonacci(int input) {
        if (input < 0 || input > 10) {
            throw new IllegalArgumentException(
                "Fibonacci accepts integers from 0 to 10."
            );
        }

        Result result = new Result(512, input + 1);

        long start = System.nanoTime();
        result.value = fibonacciRecursive(input, result);
        result.elapsedNanoseconds = System.nanoTime() - start;

        return result;
    }

    public static Result binarySearch(int[] input, int target) {
        if (input == null || input.length < 1 || input.length > 50) {
            throw new IllegalArgumentException(
                "Enter between 1 and 50 sorted numbers."
            );
        }

        // Validate and copy before timing the search.
        int[] numbers = new int[input.length];

        for (int i = 0; i < input.length; i++) {
            numbers[i] = input[i];

            if (i > 0 && input[i - 1] > input[i]) {
                throw new IllegalArgumentException(
                    "Binary search requires ascending input. " +
                    "The value at index " + (i - 1) +
                    " is greater than the value at index " + i + "."
                );
            }
        }

        // More than enough for at most 50 input values.
        Result result = new Result(256, 16);

        long start = System.nanoTime();

        result.value = binarySearchRecursive(
            numbers, target, 0, numbers.length - 1, result
        );

        result.elapsedNanoseconds = System.nanoTime() - start;

        return result;
    }

    private static long factorialRecursive(int n, Result trace) {
        trace.enter("factorial(" + n + ")");

        trace.baseChecks++;
        boolean baseCase = n <= 1;

        trace.record(
            "CALL factorial(" + n + "). Check " +
            n + " <= 1: " + baseCase + "."
        );

        if (baseCase) {
            trace.leave();
            trace.record(
                "RETURN 1 from factorial(" + n + "). Base case."
            );
            return 1;
        }

        long smaller = factorialRecursive(n - 1, trace);
        long answer = n * smaller;

        trace.multiplications++;
        trace.leave();

        trace.record(
            "RETURN from factorial(" + n + "): " +
            n + " × " + smaller + " = " + answer + "."
        );

        return answer;
    }

    private static long fibonacciRecursive(int n, Result trace) {
        trace.enter("fibonacci(" + n + ")");

        trace.baseChecks++;
        boolean baseCase = n <= 1;

        trace.record(
            "CALL fibonacci(" + n + "). Check " +
            n + " <= 1: " + baseCase + "." +
            (baseCase
                ? ""
                : " Evaluate F(" + (n - 1) +
                  "), then F(" + (n - 2) + ").")
        );

        if (baseCase) {
            trace.leave();
            trace.record(
                "RETURN " + n + " from fibonacci(" + n + "). Base case."
            );
            return n;
        }

        long left = fibonacciRecursive(n - 1, trace);
        long right = fibonacciRecursive(n - 2, trace);

        long answer = left + right;
        trace.additions++;

        trace.leave();

        trace.record(
            "RETURN from fibonacci(" + n + "): " +
            left + " + " + right + " = " + answer + "."
        );

        return answer;
    }

    private static int binarySearchRecursive(
            int[] numbers,
            int target,
            int low,
            int high,
            Result trace) {

        trace.enter(
            "search(target=" + target +
            ", low=" + low + ", high=" + high + ")"
        );

        trace.baseChecks++;
        boolean emptyRange = low > high;

        trace.record(
            "CALL search with inclusive range [" + low + ", " + high +
            "]. Check low > high: " + emptyRange + "."
        );

        if (emptyRange) {
            trace.leave();
            trace.record(
                "RETURN -1: the search range is empty. Target not found."
            );
            return -1;
        }

        int middle = low + (high - low) / 2;
        int middleValue = numbers[middle];

        trace.valueComparisons++;

        if (middleValue == target) {
            trace.record(
                "Middle index = " + middle +
                ", value = " + middleValue +
                ". Compare value == target: true. Match found."
            );

            trace.leave();
            trace.record("RETURN matching index " + middle + ".");

            return middle;
        }

        trace.valueComparisons++;
        boolean searchRight = middleValue < target;

        trace.record(
            "Middle index = " + middle +
            ", value = " + middleValue + ".\n" +
            "Compare value == target: false.\n" +
            "Compare value < target: " + searchRight + ".\n" +
            (searchRight
                ? "Search right half [" + (middle + 1) + ", " + high + "]."
                : "Search left half [" + low + ", " + (middle - 1) + "].")
        );

        int answer;

        if (searchRight) {
            answer = binarySearchRecursive(
                numbers, target, middle + 1, high, trace
            );
        } else {
            answer = binarySearchRecursive(
                numbers, target, low, middle - 1, trace
            );
        }

        trace.leave();

        trace.record(
            "RETURN " + answer +
            " through the caller for range [" + low + ", " + high + "]."
        );

        return answer;
    }
}