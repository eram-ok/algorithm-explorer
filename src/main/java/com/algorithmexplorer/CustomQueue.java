package com.algorithmexplorer;

public class CustomQueue {

    private final int[] values;

    // Index of the oldest active item.
    private int frontIndex = 0;

    // Index where the next enqueue will write.
    private int nextInsertIndex = 0;

    private int size = 0;

    public static class Operation {
        public final boolean successful;
        public final String message;
        public final String steps;

        public final int checks;
        public final int reads;
        public final int writes;
        public final int indexUpdates;
        public final int sizeUpdates;

        public final long elapsedNanoseconds;

        public Operation(
                boolean successful,
                String message,
                String steps,
                int checks,
                int reads,
                int writes,
                int indexUpdates,
                int sizeUpdates,
                long elapsedNanoseconds) {

            this.successful = successful;
            this.message = message;
            this.steps = steps;
            this.checks = checks;
            this.reads = reads;
            this.writes = writes;
            this.indexUpdates = indexUpdates;
            this.sizeUpdates = sizeUpdates;
            this.elapsedNanoseconds = elapsedNanoseconds;
        }

        public int getCountedOperations() {
            return checks + reads + writes + indexUpdates + sizeUpdates;
        }
    }

    public CustomQueue(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException(
                "Queue capacity must be at least 1."
            );
        }

        values = new int[capacity];
    }

    public Operation enqueue(int value) {
        int oldInsert = nextInsertIndex;
        int oldSize = size;

        long start = System.nanoTime();

        boolean full = size == values.length;

        if (!full) {
            values[nextInsertIndex] = value;
            nextInsertIndex = (nextInsertIndex + 1) % values.length;
            size++;
        }

        long elapsed = System.nanoTime() - start;

        if (full) {
            return new Operation(
                false,
                "Overflow: the queue is full. Nothing was added.",
                "1. Check whether size equals capacity.\n" +
                "2. The queue is full, so reject the enqueue.\n" +
                "3. Leave all values and indexes unchanged.",
                1, 0, 0, 0, 0, elapsed
            );
        }

        return new Operation(
            true,
            "Enqueued " + value + ".",
            "1. Check whether the queue is full: no.\n" +
            "2. Write " + value + " into values[" + oldInsert + "].\n" +
            "3. Update nextInsert: (" + oldInsert + " + 1) % " +
                values.length + " = " + nextInsertIndex + ".\n" +
            "4. Increase size from " + oldSize + " to " + size + ".",
            1, 0, 1, 1, 1, elapsed
        );
    }

    public Operation dequeue() {
        int oldFront = frontIndex;
        int oldSize = size;
        int removed = 0;

        long start = System.nanoTime();

        boolean empty = size == 0;

        if (!empty) {
            removed = values[frontIndex];
            frontIndex = (frontIndex + 1) % values.length;
            size--;
        }

        long elapsed = System.nanoTime() - start;

        if (empty) {
            return new Operation(
                false,
                "Underflow: the queue is empty. Nothing was removed.",
                "1. Check whether size equals zero.\n" +
                "2. The queue is empty, so reject the dequeue.\n" +
                "3. Leave the queue unchanged.",
                1, 0, 0, 0, 0, elapsed
            );
        }

        return new Operation(
            true,
            "Dequeued " + removed + ".",
            "1. Check whether the queue is empty: no.\n" +
            "2. Read " + removed + " from values[" + oldFront + "].\n" +
            "3. Update front: (" + oldFront + " + 1) % " +
                values.length + " = " + frontIndex + ".\n" +
            "4. Decrease size from " + oldSize + " to " + size + ".\n" +
            "5. Return " + removed + ". No remaining items are shifted.",
            1, 1, 0, 1, 1, elapsed
        );
    }

    public Operation front() {
        return inspect(false);
    }

    public Operation rear() {
        return inspect(true);
    }

    private Operation inspect(boolean inspectRear) {
        int index = -1;
        int value = 0;

        long start = System.nanoTime();

        boolean empty = size == 0;

        if (!empty) {
            index = inspectRear
                ? (nextInsertIndex - 1 + values.length) % values.length
                : frontIndex;

            value = values[index];
        }

        long elapsed = System.nanoTime() - start;

        String name = inspectRear ? "Rear" : "Front";

        if (empty) {
            return new Operation(
                false,
                "The queue is empty. There is no " +
                    name.toLowerCase() + " item.",
                "1. Check whether size equals zero.\n" +
                "2. The queue is empty, so no item can be read.",
                1, 0, 0, 0, 0, elapsed
            );
        }

        return new Operation(
            true,
            name + " item: " + value + ". Queue unchanged.",
            "1. Check whether the queue is empty: no.\n" +
            "2. Locate the " + name.toLowerCase() +
                " item at index " + index + ".\n" +
            "3. Read and return " + value +
                " without changing queue state.",
            1, 1, 0, 0, 0, elapsed
        );
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return values.length;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == values.length;
    }

    public int getFrontIndex() {
        return frontIndex;
    }

    public int getNextInsertIndex() {
        return nextInsertIndex;
    }

    public int getRearIndex() {
        if (isEmpty()) {
            return -1;
        }

        return (nextInsertIndex - 1 + values.length) % values.length;
    }

    // Used for displaying physical array positions.
    public boolean isActiveIndex(int index) {
        if (index < 0 || index >= values.length) {
            return false;
        }

        int distanceFromFront =
            (index - frontIndex + values.length) % values.length;

        return distanceFromFront < size;
    }

    public int valueAt(int index) {
        if (!isActiveIndex(index)) {
            throw new IndexOutOfBoundsException(
                "This slot is not part of the active queue."
            );
        }

        return values[index];
    }

    // Used for displaying queue order from oldest to newest.
    public int valueAtPosition(int position) {
        if (position < 0 || position >= size) {
            throw new IndexOutOfBoundsException(
                "Invalid logical queue position."
            );
        }

        int index = (frontIndex + position) % values.length;
        return values[index];
    }
}