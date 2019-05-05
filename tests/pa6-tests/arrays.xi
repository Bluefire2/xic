use io
use conv

main(args: int[][]) {
    counter: int = 0;
    i: int = 0;
    while (i < 1000000) {
        strings: int[][] = {
            "Hello World!",
            "Goodbye World :(",
            "Why is this class so hard",
            "I hope this ends up being a good test case",
            "xixixixixixixixixixixixixixixi",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        };

        j: int = 0;
        while (j < length(strings)) {
            string: int[] = strings[j];
            k: int = 0;
            while (k < length(string)) {
                counter = counter + string[k];
                k = k + 1;
            }
            j = j + 1;
        }

        i = i + 1;
    }

    println(unparseInt(counter));
}