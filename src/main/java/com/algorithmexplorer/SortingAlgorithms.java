public static SortTrace mergeSort(int[] input) {
        int[] values = SortTrace.copy(input);
        SortTrace trace = new SortTrace(values.length);
        long startTime = System.nanoTime();

        trace.record(values, "Initial array: " + SortTrace.format(values), -1, -1, null);

        if (values.length > 1) {
            mergeSortRecursive(values, 0, values.length - 1, trace);
        }

        trace.setElapsedNanoseconds(System.nanoTime() - startTime);
        trace.record(values, "Merge Sort complete: " + SortTrace.format(values), -1, -1, null);
        return trace;
    }

    private static void mergeSortRecursive(int[] values, int left, int right, SortTrace trace) {
        if (left >= right) {
            return;
        }

        int mid = left + (right - left) / 2;
        trace.record(
            values,
            "Dividing subarray from index " + left + " to " + right + " (mid = " + mid + ")",
            left,
            right,
            null
        );

        mergeSortRecursive(values, left, mid, trace);
        mergeSortRecursive(values, mid + 1, right, trace);
        merge(values, left, mid, right, trace);
    }

    private static void merge(int[] values, int left, int mid, int right, SortTrace trace) {
        int leftSize = mid - left + 1;
        int rightSize = right - mid;

        int[] leftArr = new int[leftSize];
        int[] rightArr = new int[rightSize];

        for (int i = 0; i < leftSize; i++) {
            leftArr[i] = values[left + i];
        }
        for (int j = 0; j < rightSize; j++) {
            rightArr[j] = values[mid + 1 + j];
        }

        int i = 0;
        int j = 0;
        int k = left;

        while (i < leftSize && j < rightSize) {
            trace.countComparison();
            trace.record(
                values,
                "Comparing " + leftArr[i] + " and " + rightArr[j],
                left + i,
                mid + 1 + j,
                null
            );

            if (leftArr[i] <= rightArr[j]) {
                values[k] = leftArr[i];
                trace.countWrite();
                trace.record(
                    values,
                    "Wrote " + leftArr[i] + " at index " + k,
                    k,
                    -1,
                    leftArr[i]
                );
                i++;
            } else {
                values[k] = rightArr[j];
                trace.countWrite();
                trace.record(
                    values,
                    "Wrote " + rightArr[j] + " at index " + k,
                    k,
                    -1,
                    rightArr[j]
                );
                j++;
            }
            k++;
        }

        while (i < leftSize) {
            values[k] = leftArr[i];
            trace.countWrite();
            trace.record(
                values,
                "Copied remaining left value " + leftArr[i] + " to index " + k,
                k,
                -1,
                leftArr[i]
            );
            i++;
            k++;
        }

        while (j < rightSize) {
            values[k] = rightArr[j];
            trace.countWrite();
            trace.record(
                values,
                "Copied remaining right value " + rightArr[j] + " to index " + k,
                k,
                -1,
                rightArr[j]
            );
            j++;
            k++;
        }
    }