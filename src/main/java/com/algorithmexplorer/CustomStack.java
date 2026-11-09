package com.algorithmexplorer;

public class CustomStack {

    private final int[] values;
    private int top = -1;

    public static class Operation {
        public final boolean successful;
        public final String message;
        public final String steps;
        public final int checks;
        public final int reads;
        public final int writes;
        public final int indexUpdates;
        public final long elapsedNanoseconds;

        public Operation(
                boolean successful,
                String message,
                String steps,
                int checks,
                int reads,
                int writes,
                int indexUpdates,
                long elapsedNanoseconds) {

            this.successful = successful;
            this.message = message;
            this.steps = steps;
            this.checks = checks;
            this.reads = reads;
            this.writes = writes;
            this.indexUpdates = indexUpdates;
            this.elapsedNanoseconds = elapsedNanoseconds;
        }

        public int getCountedOperations() {
            return checks + reads + writes + indexUpdates;
        }
    }

    public CustomStack(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException(
                "Stack capacity must be at least 1."
            );
        }

        values = new int[capacity];
    }

    public Operation push(int value) {
        int previousTop = top;

        long start = System.nanoTime();

        boolean full = top == values.length - 1;

        if (!full) {
            top++;
            values[top] = value;
        }

        long elapsed = System.nanoTime() - start;

        // Explanations are constructed after timing the operation.
        if (full) {
            return new Operation(
                false,
                "Overflow: the stack is full. Nothing was added.",
                "1. Check whether top equals capacity - 1.\n" +
                "2. The stack is full, so reject the push.\n" +
                "3. Keep the stack unchanged.",
                1, 0, 0, 0, elapsed
            );
        }

        return new Operation(
            true,
            "Pushed " + value + ".",
            "1. Check whether the stack is full: no.\n" +
            "2. Increase top from " + previousTop + " to " + top + ".\n" +
            "3. Write " + value + " into values[" + top + "].",
            1, 0, 1, 1, elapsed
        );
    }

    public Operation pop() {
        int previousTop = top;
        int removedValue = 0;

        long start = System.nanoTime();

        boolean empty = top == -1;

        if (!empty) {
            removedValue = values[top];
            top--;
        }

        long elapsed = System.nanoTime() - start;

        if (empty) {
            return new Operation(
                false,
                "Underflow: the stack is empty. Nothing was removed.",
                "1. Check whether top equals -1.\n" +
                "2. The stack is empty, so reject the pop.\n" +
                "3. Keep the stack unchanged.",
                1, 0, 0, 0, elapsed
            );
        }

        return new Operation(
            true,
            "Popped " + removedValue + ".",
            "1. Check whether the stack is empty: no.\n" +
            "2. Read " + removedValue +
                " from values[" + previousTop + "].\n" +
            "3. Decrease top from " + previousTop + " to " + top + ".\n" +
            "4. Return " + removedValue +
                ". The old array slot is outside the active stack.",
            1, 1, 0, 1, elapsed
        );
    }

    public Operation peek() {
        int topValue = 0;

        long start = System.nanoTime();

        boolean empty = top == -1;

        if (!empty) {
            topValue = values[top];
        }

        long elapsed = System.nanoTime() - start;

        if (empty) {
            return new Operation(
                false,
                "The stack is empty. There is no top item.",
                "1. Check whether top equals -1.\n" +
                "2. The stack is empty, so no value can be read.",
                1, 0, 0, 0, elapsed
            );
        }

        return new Operation(
            true,
            "Top item: " + topValue + ". The stack is unchanged.",
            "1. Check whether the stack is empty: no.\n" +
            "2. Read " + topValue + " from values[" + top + "].\n" +
            "3. Return the value without changing top.",
            1, 1, 0, 0, elapsed
        );
    }

    public int size() {
        return top + 1;
    }

    public int capacity() {
        return values.length;
    }

    public int getTopIndex() {
        return top;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == values.length - 1;
    }

    // Used by the interface to display active stack elements.
    public int valueAt(int index) {
        if (index < 0 || index > top) {
            throw new IndexOutOfBoundsException(
                "This index is outside the active stack."
            );
        }

        return values[index];
    }
}