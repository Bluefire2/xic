// Adapted from https://www.geeksforgeeks.org/merge-sort/

// Merge subarrays arr[l..m] and arr[m+1..r]
merge(arr: int[], l: int, m: int, r: int) {
    // Find sizes of two subarrays to be merged
    n1:int = m - l + 1
    n2:int = r - m

    // Create temp arrays
    L:int[n1]
    R:int[n2]

    // Copy data to temp arrays
    i:int = 0
    while (i < n1) {
        L[i] = arr[l + i]
        i = i + 1
    }

    j:int = 0
    while (j < n2) {
        R[j] = arr[m + 1 + j]
        j = j + 1
    }

    // Merge the temp arrays

    // Initial indexes of first and second subarrays
    i = 0
    j = 0

    k:int = l   // Initial index of merged subarray
    while (i < n1 & j < n2) {
        if (L[i] <= R[j]) {
            arr[k] = L[i]
            i = i + 1
        } else {
            arr[k] = R[j]
            j = j+1
        }
    }

    // Copy remaining elements of L[] if any
    while (i < n1) {
        arr[k] = L[i]
        i = i + 1
        k = k + 1
    }

    // Copy remaining elements of R[] if any
    while (j < n2) {
        arr[k] = R[j];
        j = j + 1
        k = k + 1
    }
}

sort(arr:int[], l:int, r:int) {
    if (l < r) {
        // Find the middle point
        m:int = (l + r) / 2

        // Sort the first and second halves
        sort(arr, l, m)
        sort(arr, m+1, r)

        // Merge the sorted halves
        merge(arr, l, m, r)
    }
}
