// loops and recursion (direct and mutual).
// will get array index out of bounds if generated code is incorrect
even(x: int) : bool {
  if (x == 0) { return true }
  else { return odd(x-1) } }
odd(x: int) : bool {
  if (x == 1) { return true }
  else { return even(x-1) } }
even_self(x: int) : bool {
  if (x == 0) { return true }
  else if (x == 1) { return false }
  else { return even_self(x-2) } }
main(args: int[][]) { y : int
  if (!even(34328)) { y = args[35][57] }
  else if (!odd(32227)) { y = args[35][57] }
  else if (!even_self(34328)) { y = args[35][57]}
  a : int = 32227; a_even : bool = true
  while (a >= 0) {
    if (a == 0) { a_even = true }
    else if (a == 1) { a_even = false }
    a = a - 2 }
  if (a_even) { y = args[35][57] }             }
