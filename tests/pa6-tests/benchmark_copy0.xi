use io; use conv; //similar to lotsofvars
fn(count: int, input: int):int{if(count == 0){return 100;}
a: int = 5;b: int = a;c: int = b;d: int = b + 5;e: int = c + 10;f: int = a + c;
g: int = d;h: int = a;i: int = h;rax: int = 5;rsp: int = i;l: int = e + c;
m: int = e - c + b - a;n: int = h - a + c - l;o: int = n;p: int = o;q: int = p;
r: int = q;s: int = r;t: int = s;u: int = t;v: int = s + p + q;w: int = t;
x: int = a + c - p;y: int = rsp + a + o + r;z: int = rsp;
return fn(count - 1, input + a * b - c * d + e * f - g * h + i * rax - rsp * l
+ m * n - o * p + q);}
main(args: int[][]) {println("I'm not sorry");counter: int = 0;acc: int = 0;
println(unparseInt(counter));println(unparseInt(acc));while (counter<500000)
{a: int = 5;b: int = a;c: int = b;d: int = b + 5;e: int = c + 10;f: int = a + c;
g: int = d;h: int = a;i: int = h;rax: int = 5rsp: int = i;l: int = e + c;
m: int = e - c + b - a;n: int = h - a + c - l;o: int = n;p: int = o;q: int = p;
r: int = q;s: int = r;t: int = s;u: int = t;v: int = s + p + q;w: int = t;
x: int = a + c - p;y: int = rsp - a + o - r;z: int = rsp;result: int =
fn(100, a + b + c + d + e + f + g + h + i + rax + rsp);acc = acc + result;counter = counter + 1;}
println(unparseInt(acc));}