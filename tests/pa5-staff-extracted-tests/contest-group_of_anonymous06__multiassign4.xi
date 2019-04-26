use io; use conv
foo(a:int, b:int, c:int, d:int, e:int, f:int, g:int, h:int):int,int{
	println("In foo:")
	println("a = "+unparseInt(a))
	println("b = "+unparseInt(b))
	println("c = "+unparseInt(c))
	println("d = "+unparseInt(d))
	println("e = "+unparseInt(e))
	println("f = "+unparseInt(f))
	println("g = "+unparseInt(g))
	println("h = "+unparseInt(h))
	return a+b,c+d}
main(args:int[][]){
	a:int, b:int = foo(100000000000000000,20000000000000000,3000000000000000,
	400000000000000,50000000000000,6000000000000,700000000000,80000000000)
	println("In main:")
	println("a = "+unparseInt(a))
	println("b = "+unparseInt(b))	
	println(unparseInt(a+b))}
