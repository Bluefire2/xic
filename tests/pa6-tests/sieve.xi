use io
use conv

sieve(n: int): bool {
    array: bool[n + 1];
    i: int = 0;
    while (i < n + 1) {
        array[i] = true;
        i = i + 1;
    }

    j: int = 2;
    while (j < n / 2) {
        if (array[j]) {
            k: int = j * j;
            while (k < n + 1) {
                array[k] = false;
                k = k + j;
            }
        }
        j = j + 1;
    }

    return array[n];
}

printPrime(n: int) {
    prime: bool = sieve(n);
    if (prime) {
        println("Prime!");
    } else {
        println("Not prime!");
    }
}

main(args: int[][]) {
    printPrime(21269);
    printPrime(12345);
    printPrime(19333);
    printPrime(11111);
    printPrime(31307);
}