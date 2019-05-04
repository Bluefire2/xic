use io; use conv;
id(i:int):int{j:int = i;k:int = j;return k;}
idn(i:int, n:int):int{while(n > 0) {i = id(i);n = n - 1;} return i;}
main(args:int[][]){println(unparseInt(idn(100,100000000)));}