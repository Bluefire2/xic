use conv
use io

sort(a: int[]) {
    i:int = 0
    n:int = length(a)
    while i < n {
        j:int = i
        while j > 0 {
            if a[j-1] > a[j] {
                swap:int = a[j]
                a[j] = a[j-1]
                a[j-1] = swap
            }
            j = j-1
        }
        i = i+1
    }
}

main(args:int[][]) {
    //alphabetically sort strings
    t:int[][]= {"DFGBAEC","BEAGCFDZ","FDBAEGCZZ"}
    i:int = 0
    while i < length(t) {
        sort(t[i])
        i = i + 1
    }
    i = 0
    while i < length(t) {
        println(t[i])
        i = i + 1
    }
}

