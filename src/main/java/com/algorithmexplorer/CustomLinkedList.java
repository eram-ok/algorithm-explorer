package com.algorithmexplorer;

public class CustomLinkedList {

    private static class Node {
        private final int value;
        private Node next;

        private Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private int size;

    // Keeps the teaching visualization manageable.
    private static final int MAX_SIZE = 50;

    public static class Operation {
        public final boolean successful;
        public final String message;
        public final String steps;
        public final int nodesVisited;
        public final int linkWrites;
        public final long elapsedNanoseconds;

        public Operation(
                boolean successful,
                String message,
                String steps,
                int nodesVisited,
                int linkWrites,
                long elapsedNanoseconds) {

            this.successful = successful;
            this.message = message;
            this.steps = steps;
            this.nodesVisited = nodesVisited;
            this.linkWrites = linkWrites;
            this.elapsedNanoseconds = elapsedNanoseconds;
        }

        public int getCountedOperations() {
            return nodesVisited + linkWrites;
        }
    }

    public Operation insertFirst(int value) {
        long start = System.nanoTime();

        if (size == MAX_SIZE) {
            return result(
                false,
                "Display limit reached: maximum 50 nodes.",
                "Check the visualization limit. No node was inserted.",
                0, 0, start
            );
        }

        Node node = new Node(value);

        node.next = head;
        head = node;
        size++;

        return result(
            true,
            "Inserted " + value + " at the beginning.",
            "1. Create a node containing " + value + ".\n" +
            "2. Set the new node's next reference to the old head.\n" +
            "3. Update head to point to the new node.\n" +
            "4. Increase the list size.",
            0, 2, start
        );
    }

    public Operation insertLast(int value) {
        long start = System.nanoTime();

        if (size == MAX_SIZE) {
            return result(
                false,
                "Display limit reached: maximum 50 nodes.",
                "Check the visualization limit. No node was inserted.",
                0, 0, start
            );
        }

        Node node = new Node(value);
        StringBuilder steps = new StringBuilder(
            "1. Create a new node. Its next reference is null.\n"
        );

        int visits = 0;

        if (head == null) {
            head = node;
            steps.append("2. The list is empty. Set head to the new node.\n");
        } else {
            Node current = head;
            int index = 0;

            while (true) {
                visits++;

                steps.append("Visit index ")
                     .append(index)
                     .append(", value ")
                     .append(current.value)
                     .append(".\n");

                if (current.next == null) {
                    break;
                }

                current = current.next;
                index++;
            }

            current.next = node;
            steps.append("Set the last node's next to the new node.\n");
        }

        size++;
        steps.append("Increase the list size.");

        return result(
            true,
            "Inserted " + value + " at the end.",
            steps.toString(),
            visits, 1, start
        );
    }

    public Operation search(int target) {
        long start = System.nanoTime();

        Node current = head;
        int index = 0;
        int visits = 0;
        StringBuilder steps = new StringBuilder();

        while (current != null) {
            visits++;

            steps.append("Visit index ")
                 .append(index)
                 .append(": compare ")
                 .append(current.value)
                 .append(" with ")
                 .append(target)
                 .append(".\n");

            if (current.value == target) {
                steps.append("Match found. Stop searching.");

                return result(
                    true,
                    "Found " + target + " at index " + index + ".",
                    steps.toString(),
                    visits, 0, start
                );
            }

            current = current.next;
            index++;
        }

        steps.append("Reached null. The value is not in the list.");

        return result(
            false,
            target + " was not found.",
            steps.toString(),
            visits, 0, start
        );
    }

    public Operation deleteFirstMatch(int target) {
        long start = System.nanoTime();

        Node current = head;
        Node previous = null;

        int index = 0;
        int visits = 0;
        StringBuilder steps = new StringBuilder();

        while (current != null) {
            visits++;

            steps.append("Visit index ")
                 .append(index)
                 .append(": compare ")
                 .append(current.value)
                 .append(" with ")
                 .append(target)
                 .append(".\n");

            if (current.value == target) {
                if (previous == null) {
                    head = current.next;
                    steps.append(
                        "The match is the head. Set head to head.next.\n"
                    );
                } else {
                    previous.next = current.next;
                    steps.append(
                        "Set previous.next to current.next, " +
                        "bypassing the matched node.\n"
                    );
                }

                size--;
                steps.append(
                    "Decrease the list size. Stop after this first match."
                );

                return result(
                    true,
                    "Deleted the first " + target +
                    ", previously at index " + index + ".",
                    steps.toString(),
                    visits, 1, start
                );
            }

            previous = current;
            current = current.next;
            index++;
        }

        steps.append("Reached null. No links were changed.");

        return result(
            false,
            target + " was not found. Nothing was deleted.",
            steps.toString(),
            visits, 0, start
        );
    }

    private Operation result(
            boolean successful,
            String message,
            String steps,
            int visits,
            int linkWrites,
            long start) {

        long elapsed = System.nanoTime() - start;

        return new Operation(
            successful,
            message,
            steps,
            visits,
            linkWrites,
            elapsed
        );
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return head == null;
    }

    /*
     * A display snapshot only.
     * The actual linked list is stored in nodes, not in this array.
     */
    public int[] snapshot() {
        int[] values = new int[size];
        Node current = head;
        int index = 0;

        while (current != null) {
            values[index] = current.value;
            current = current.next;
            index++;
        }

        return values;
    }
}