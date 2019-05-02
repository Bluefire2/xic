use io
use conv

quicksort(array: int[], lo: int, hi: int) {
    if (lo < hi) {
        p: int = partition(array, lo, hi);
        quicksort(array, lo, p - 1);
        quicksort(array, p + 1, hi);
    }
}

partition(array: int[], lo: int, hi: int): int {
    pivot: int = array[hi];
    i: int = lo;
    j: int = lo;
    while (j < hi) {
        if (array[j] < pivot) {
            // swap array[i] with array[j]
            swap(array, i, j);
            i = i + 1;
        }
        j = j + 1;
    }
    swap(array, i, hi);
    return i;
}

swap(array: int[], i: int, j: int) {
    temp: int = array[j];
    array[j] = array[i];
    array[i] = temp;
}

main(args: int[][]) {
    a: int[] = {5, 4, 3, 2, 1};
    quicksort(a, 0, 4);
    i: int = 0;
    while (i < length(a)) {
        println(unparseInt(a[i]));
        i = i + 1;
    }
}