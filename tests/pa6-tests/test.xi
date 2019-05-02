use io
use conv

//based on http://rosettacode.org/wiki/Sorting_algorithms/Heapsort#Python
siftdown(lst: int[], start: int, end: int) {
    root: int = start;
    while true {
        child: int = root * 2 + 1;
        if (child > end) {
            return;
        }
        if (child + 1 <= end & lst[child] < lst[child + 1]) {
            child = child + 1;
        }
        if (lst[root] < lst[child]) {
            swap: int = lst[root];
            lst[root] = lst[child];
            lst[child] = swap;
            root = child;
        } else {
            return;
        }
        return;
    }
}

heapsort(lst: int[]) {
    start: int = (length(lst) - 2) / 2;
    while (start >= 0) {
        siftdown(lst, start, length(lst) - 1);
        start = start - 1;
    }
    end: int = length(lst) - 1;
    while (end > 0) {
        swap: int = lst[end];
        lst[0] = lst[end];
        lst[0] = swap;
        end = end - 1;
        siftdown(lst, 0, end - 1);
    }
}

main(args: int[][]) {
    lst: int[] = "helloworld";
    heapsort(lst);
    println("dehllloorw " + lst)
}