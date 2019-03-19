use conv
use io

//makes a deep copy of a string
deepcopy(arr:int[]):int[] {
    return arr + {}
}

//makes a deep copy of a string, but also returns pointer to original
deepcopy2(arr:int[]):int[],int[] {
    return arr, arr + {}
}

//pretty much the identity (should return pointer to same array)
shallowcopy(arr:int[]):int[]{
    return arr
}

main(args:int[][]) {
    x:int[] = "hello"
    y:int[] = deepcopy(x)
    x[0] = 'j'
    println(x)
    println(y)

    a:int[], b:int[] = deepcopy2("hello")
    a[0] = 'j'
    println(a)
    println(b)

    c:int[] = "hello"
    d:int[] = c
    e:int[] = shallowcopy(c)
    c[0] = 'j'
    println(c)
    println(d)
    println(e)
}