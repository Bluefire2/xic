use io; use conv; main(args: int[][]) {loop: int = 0; counter: int = 0;
while (loop < 20000000) { a: int = 5; b: int = a+5; c: int = a+b+10;
d: int = a+b+c-8; e: int = a-b+c*d+3;f: int = a-b+c*d-e+6;
g: int = a-b+c*d-e*f+7; h: int = a-b+c*d-e*f-g-9;i: int = a-b+c*d-e*f-g / h-17;
j: int = a-b+c*d-e*f-g / h+i-17;k: int = 5;l: int = k+5;n: int = k+l+10;
o: int = k+l+n-8;p: int = k-l+n*o+3;q: int = k-l+n*o-p+6;r: int = k-l+n*o-p*q+7;
s: int = k-l+n*o-p*q-r-9;t: int = k-l+n*o-p*q-r / s-17;
u: int = k-l+n*o-p*q-r / s+t-17;counter = counter+u+j;loop = loop+1;}
println(unparseInt(counter));}