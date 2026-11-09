package com.algorithmexplorer;

public class RecursionAlgorithms {

    public static class Step {
        public final String description;
        public final String activeStack;
        public final long calls;
        public final long baseChecks;
        public final long multiplications;
        public final int activeDepth;

        public Step(
                String description,
                String activeStack,
                long calls,
                long baseChecks,
                long multiplications,
                int activeDepth) {

            this.description = description;
            this.activeStack = activeStack;
            this.calls = calls;
            this.baseChecks = baseChecks;
            this.multiplications = multiplications;
            this.activeDepth = activeDepth;
        }
    }

    public static class Result {
        private final Step[] steps;
        private int stepCount;

        private final int[] activeArguments;
        private int activeSize;

        private long value;
        private long calls;
        private long baseChecks;
        private long multiplications;
        private int maximumDepth;
        private long elapsedNanoseconds;

        private Result(int input) {
            // Each invocation produces one call step and one return step.
            steps = new Step[2 * (input + 1)];
            activeArguments = new int[input + 1];
        }

        private void record(String description) {
            StringBuilder stack = new StringBuilder();

            if (activeSize == 0) {
                stack.append("Empty — all calls have returned.");
            } else {
                // Most recent invocation appears at the top.
                for (int i = activeSize - 1; i >= 0; i--) {
                    if (i == activeSize - 1) {
                        stack.append("TOP → ");
                    } else {
                        stack.append("      ");
                    }

                    stack.append("factorial(")
                         .append(activeArguments[i])
                         .append(")");

                    if (i == activeSize - 1) {
                        stack.append(" — current call");
                    } else {
                        stack.append(" — waiting for a result");
                    }

                    stack.append("\n");
                }
            }

            steps[stepCount++] = new Step(
                description,
                stack.toString(),
                calls,
                baseChecks,
                multiplications,
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

        public int getMaximumDepth() {
            return maximumDepth;
        }

        public long getElapsedNanoseconds() {
            return elapsedNanoseconds;
        }
    }

    public static Result factorial(int input) {
        if (input < 0 || input > 20) {
            throw new IllegalArgumentException(
                "Enter an integer from 0 to 20."
            );
        }

        Result result = new Result(input);

        long start = System.nanoTime();
        result.value = factorialRecursive(input, result);
        result.elapsedNanoseconds = System.nanoTime() - start;

        return result;
    }

    private static long factorialRecursive(int n, Result trace) {
        trace.calls++;

        trace.activeArguments[trace.activeSize] = n;
        trace.activeSize++;

        if (trace.activeSize > trace.maximumDepth) {
            trace.maximumDepth = trace.activeSize;
        }

        trace.baseChecks++;
        boolean baseCase = n <= 1;

        trace.record(
            "CALL factorial(" + n + "). Check whether " + n +
            " <= 1: " + baseCase + "."
        );

        if (baseCase) {
            trace.activeSize--;

            trace.record(
                "RETURN 1 from factorial(" + n +
                "). This is the base case."
            );

            return 1;
        }

        // The actual recursive call.
        long smallerResult = factorialRecursive(n - 1, trace);

        long answer = n * smallerResult;
        trace.multiplications++;

        trace.activeSize--;

        trace.record(
            "RETURN from factorial(" + n + "): " +
            n + " × " + smallerResult + " = " + answer + "."
        );

        return answer;
    }
}