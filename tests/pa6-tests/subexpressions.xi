use io
use conv

main(args: int[][]) {
    i: int = 0;

    a: int = 123456789;
    b: int = 987654321;
    prime: int = 4725067;
    biggerPrime: int = 842580419;
    array: int[] = "Some string goes here";

    counter: int = 0;

    while (i < 20000000) {
        f: int = (biggerPrime / prime) / (b / a) + 1;
        g: int = (biggerPrime / prime) / (b / a) - 78;
        h: int = 1 + (biggerPrime / prime) / (b / a) - array[6];
        j: int = array[10] * array[7] + (biggerPrime / prime) / (b / a) - 8;
        k: int = (biggerPrime / prime) / (b / a) - 7 + array[10] * array[7];
        l: int = (biggerPrime / prime) / (b / a) - array[0] + array[9];
        m: int = 10 + (biggerPrime / prime) / (b / a) - 99;
        n: int = array[2] + (biggerPrime / prime) / (b / a) - 7;
        o: int = array[3] - array[2] + (biggerPrime / prime) / (b / a) + 55;
        p: int = array[3] + array[7] + (biggerPrime / prime) / (b / a) - 55;

        i = i + 1;
        counter = counter + f - g + h - j + k - l + m - n + o - p;
    }

    println(unparseInt(counter));
}