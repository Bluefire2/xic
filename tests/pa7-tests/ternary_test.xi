use io
use conv

main(args:int[][]){
x:int[] = "a";
y:int[] = "b";
z:int[] = (x == y) ? x : y;
zz:int[] = (x != y) ? x : y;
print(z);
println(zz);
//should print out ba
}