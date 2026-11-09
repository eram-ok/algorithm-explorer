package com.algorithmexplorer;

public class RecursionAlgorithms {

    public static class Step {
        public final String description;
        public final String activeStack;
        public final long calls;
        public final long baseChecks;
        public final long multiplications;
        public final long additions;
        public final int activeDepth;

        public Step(
                String description,
                String activeStack,
                long calls,
                long baseChecks,
                long multiplications,
                long additions,
                int activeDepth) {

            this.description = description;
            this.activeStack = activeStack;
            this.calls = calls;
            this.baseChecks = baseChecks;
            this.multiplications = multiplications;
            this.additions = additions;
            this.activeDepth = activeDepth;
        }
    }

    public static class Result {
        private final String functionName;
        private final Step[] steps;
        private int stepCount;

        private final int[] activeArguments;
        private int activeSize;

        private long value;
        private long calls;
        private long baseChecks;
        private long multiplications;
        private long additions;
        private int maximumDepth;
        private long elapsedNanoseconds;

        private Result(
                String functionName,
                int input,
                int stepCapacity) {

            this.functionName = functionName;
            steps = new Step[stepCapacity];
            activeArguments = new int[input + 1];
        }

        private void enter(int argument) {
            calls++;

            activeArguments[activeSize] = argument;
            activeSize++;

            if (activeSize > maximumDepth) {
                maximumDepth = activeSize;
            }
        }

        private void record(String description) {
            if (stepCount == steps.length) {
                throw new IllegalStateException(
                    "Trace capacity exceeded."
                );
            }

            StringBuilder stack = new StringBuilder();

            if (activeSize == 0) {
                stack.append("Empty — all calls have returned.");
            } else {
                for (int i = activeSize - 1; i >= 0; i--) {
                    stack.append(
                        i == activeSize - 1 ? "TOP → " : "      "
                    );

                    stack.append(functionName)
                         .append("(")
                         .append(activeArguments[i])
                         .append(")");

                    stack.append(
                        i == activeSize - 1
                            ? " — current call"
                            : " — waiting for a result"
                    );

                    stack.append("\n");
                }
            }

            steps[stepCount++] = new Step(
                description,
                stack.toString(),
                calls,
                baseChecks,
                multiplications,
                additions,
                activeSize
            );
        }

        public Step getStep(int index) {
            if (index < 0 || index >= stepCount) {
                throw new IndexOutOfBoundsException(
                    "Invalid step index."
                );
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
            "Choose a valid recursion algorithm."
        );
    }

    public static Result factorial(int input) {
        if (input < 0 || input > 20) {
            throw new IllegalArgumentException(
                "Factorial accepts integers from 0 to 20."
            );
        }

        Result result = new Result(
            "factorial", input, 2 * (input + 1)
        );

        long start = System.nanoTime();
        result.value = factorialRecursive(input, result);
        result.elapsedNanoseconds = System.nanoTime() - start;

        return result;
    }

    public static Result fibonacci(int input) {
        if (input < 0 || input > 10) {
            throw new IllegalArgumentException(
                "Fibonacci accepts integers from 0 to 10 " +
                "to keep playback manageable."
            );
        }

        /*
         * F(10) produces 177 calls and 354 call/return steps.
         * A fixed array of 512 steps is sufficient for the input limit.
         */
        Result result = new Result("fibonacci", input, 512);

        long start = System.nanoTime();
        result.value = fibonacciRecursive(input, result);
        result.elapsedNanoseconds = System.nanoTime() - start;

        return result;
    }

    private static long factorialRecursive(int n, Result trace) {
        trace.enter(n);

        trace.baseChecks++;
        boolean baseCase = n <= 1;

        trace.record(
            "CALL factorial(" + n + "). Check " + n +
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

    private static long fibonacciRecursive(int n, Result trace) {
        trace.enter(n);

        trace.baseChecks++;
        boolean baseCase = n <= 1;

        trace.record(
            "CALL fibonacci(" + n + "). Check " + n +
            " <= 1: " + baseCase + "." +
            (baseCase
                ? ""
                : " First evaluate fibonacci(" + (n - 1) +
                  "), then fibonacci(" + (n - 2) + ").")
        );

        if (baseCase) {
            trace.activeSize--;

            trace.record(
                "RETURN " + n + " from fibonacci(" + n +
                "). This is the base case."
            );

            return n;
        }

        // Two real recursive calls; no memoization or cached answers.
        long leftAnswer = fibonacciRecursive(n - 1, trace);
        long rightAnswer = fibonacciRecursive(n - 2, trace);

        long answer = leftAnswer + rightAnswer;
        trace.additions++;

        trace.activeSize--;

        trace.record(
            "RETURN from fibonacci(" + n + "): " +
            "F(" + (n - 1) + ") + F(" + (n - 2) + ") = " +
            leftAnswer + " + " + rightAnswer + " = " + answer + "."
        );

        return answer;
    }
}