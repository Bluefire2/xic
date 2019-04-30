// Solution to project euler 116
asser(c:bool) { if (!c) { x:int = 1/0; } }
search(memos:int[][], valid:bool[][], n:int, blen:int) : int {
  if (valid[blen - 2][n]) {
    return memos[blen-2][n]
  }
  i:int = 0 count:int = 0
  while (i < n-blen+1) {
    count = count + 1 + search(memos, valid, n - i - blen, blen) 
    i = i + 1
  }
  valid[blen-2][n] = true;
  memos[blen-2][n] = count;
  return count;
}
main(args:int[][]) {
  memos:int[3][51]
  valid:bool[3][51]
  asser(search(memos, valid, 50,2) +
        search(memos, valid, 50,3) +
        search(memos, valid, 50,4) == 20492570929) }
