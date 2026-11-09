package com.algorithmexplorer;

/** Dependency-free regression runner. See README for the exact command. */
public class AlgorithmRegressionChecks {
    private static int assertions;
    private static void check(boolean valid, String message) {
        assertions++;
        if (!valid) throw new AssertionError(message);
    }
    private static void equal(int[] a, int[] b, String message) {
        check(a.length == b.length, message + " length");
        for (int i = 0; i < a.length; i++) check(a[i] == b[i], message + " index " + i);
    }
    public static void main(String[] args) {
        String[] algorithms = {"Bubble Sort", "Selection Sort", "Insertion Sort", "Merge Sort"};
        int[] comparisons = {10, 10, 7, 8};
        int[] writes = {10, 2, 9, 24};
        int[] input = {8, 3, 5, 1, 9};
        for (int method = 0; method < algorithms.length; method++) {
            SortTrace trace = SortingAlgorithms.run(algorithms[method], input);
            equal(trace.getStep(trace.getStepCount() - 1).values, new int[]{1,3,5,8,9}, "example sort");
            check(trace.getComparisons() == comparisons[method], "comparison counter");
            check(trace.getWrites() == writes[method], "write counter");
            equal(trace.getStep(0).values, input, "initial snapshot");
            java.util.Random random = new java.util.Random(2107);
            for (int sample = 0; sample < 100; sample++) {
                int[] values = new int[1 + random.nextInt(50)];
                for (int i = 0; i < values.length; i++) values[i] = random.nextInt(21) - 10;
                int[] original = SortTrace.copy(values);
                SortTrace run = SortingAlgorithms.run(algorithms[method], values);
                int[] sorted = run.getStep(run.getStepCount() - 1).values;
                for (int i = 1; i < sorted.length; i++) check(sorted[i-1] <= sorted[i], "sorted order");
                for (int value = -10; value <= 10; value++) {
                    int before = 0, after = 0;
                    for (int n : original) if (n == value) before++;
                    for (int n : sorted) if (n == value) after++;
                    check(before == after, "same multiset");
                }
                equal(values, original, "input unchanged");
                equal(run.getStep(0).values, original, "snapshot unchanged");
            }
            equal(SortingAlgorithms.run(algorithms[method], new int[]{7}).getStep(0).values, new int[]{7}, "singleton");
        }
        CustomStack stack = new CustomStack(5);
        check(!stack.pop().successful, "empty stack pop");
        for (int i = 1; i <= 5; i++) check(stack.push(i).successful, "push");
        check(!stack.push(6).successful, "stack overflow");
        check(stack.peek().getCountedOperations() == 2, "peek counter");
        for (int i = 5; i >= 1; i--) {
            check(stack.valueAt(stack.getTopIndex()) == i, "LIFO");
            check(stack.pop().getCountedOperations() == 3, "pop counter");
        }
        check(stack.isEmpty(), "empty after pops");
        CustomQueue queue = new CustomQueue(5);
        check(!queue.dequeue().successful, "empty queue dequeue");
        for (int i = 1; i <= 5; i++) queue.enqueue(i * 10);
        check(!queue.enqueue(60).successful, "queue overflow");
        queue.dequeue(); queue.dequeue(); queue.enqueue(60); queue.enqueue(70);
        for (int i = 0; i < 5; i++) check(queue.valueAtPosition(i) == 30 + i*10, "FIFO wraparound");
        check(queue.getFrontIndex() == 2 && queue.getRearIndex() == 1, "wrapped indices");
        for (int i = 0; i < 5; i++) check(queue.dequeue().getCountedOperations() == 4, "dequeue counter");
        check(queue.isEmpty(), "empty queue");
        CustomLinkedList list = new CustomLinkedList();
        check(!list.deleteFirstMatch(10).successful, "empty list delete");
        list.insertLast(20); list.insertFirst(10); list.insertLast(30);
        check(list.search(20).nodesVisited == 2, "list search");
        list.deleteFirstMatch(20); list.insertLast(10); list.deleteFirstMatch(10);
        equal(list.snapshot(), new int[]{30,10}, "first-match deletion");
        list.deleteFirstMatch(10); list.deleteFirstMatch(30);
        check(list.isEmpty(), "list singleton deletion");
        check(RecursionAlgorithms.factorial(20).getValue() == 2432902008176640000L, "factorial 20");
        check(RecursionAlgorithms.factorial(0).getValue() == 1, "factorial zero");
        check(RecursionAlgorithms.fibonacci(10).getValue() == 55, "fibonacci result");
        check(RecursionAlgorithms.fibonacci(10).getCalls() == 177, "fibonacci calls");
        check(RecursionAlgorithms.binarySearch(new int[]{1,3,5,8,9},8).getValue() == 3, "binary search hit");
        check(RecursionAlgorithms.binarySearch(new int[]{1,3,5,8,9},7).getValue() == -1, "binary search miss");
        boolean rejected = false;
        try { RecursionAlgorithms.binarySearch(new int[]{8,1,5},5); }
        catch (IllegalArgumentException expected) { rejected = true; }
        check(rejected, "unsorted input rejected");
        System.out.println("PASS: " + assertions + " regression assertions; all algorithms and structures checked.");
    }
}
