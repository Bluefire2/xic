f(x:int, y:int[]) {
  x = 0 + 'a'
  x = 0 - 'a'
  x = 0 * 'a'
  x = 0 *>> 'a'
  x = 0 / 'a'
  x = 0 % 'a'

  x = 0 + x
  x = 0 - x
  x = 0 * x
  x = 0 *>> x
  x = 0 / x
  x = 0 % x

  x = 0 + i()
  x = 0 - i()
  x = 0 * i()
  x = 0 *>> i()
  x = 0 / i()
  x = 0 % i()

  x = 0 + y[0]
  x = 0 - y[0]
  x = 0 * y[0]
  x = 0 *>> y[0]
  x = 0 / y[0]
  x = 0 % y[0]

  x = 0 + -1
  x = 0 - -1
  x = 0 * -1
  x = 0 *>> -1
  x = 0 / -1
  x = 0 % -1

  x = 0 + (0)
  x = 0 - (0)
  x = 0 * (0)
  x = 0 *>> (0)
  x = 0 / (0)
  x = 0 % (0)

  x = 'a' + 1
  x = 'a' - 1
  x = 'a' * 1
  x = 'a' *>> 1
  x = 'a' / 1
  x = 'a' % 1

  x = x + 1
  x = x - 1
  x = x * 1
  x = x *>> 1
  x = x / 1
  x = x % 1

  x = i() + 1
  x = i() - 1
  x = i() * 1
  x = i() *>> 1
  x = i() / 1
  x = i() % 1

  x = y[0] + 1
  x = y[0] - 1
  x = y[0] * 1
  x = y[0] *>> 1
  x = y[0] / 1
  x = y[0] % 1

  x = -1 + 1
  x = -1 - 1
  x = -1 * 1
  x = -1 *>> 1
  x = -1 / 1
  x = -1 % 1

  x = (0) + 1
  x = (0) - 1
  x = (0) * 1
  x = (0) *>> 1
  x = (0) / 1
  x = (0) % 1
}

i(): int {
  return 0
}

g(x:bool, y:int, a1:bool[], a2:int[]) {
  x = 0 < 'a'
  x = 0 <= 'a'
  x = 0 >= 'a'
  x = 0 > 'a'
  x = 0 == 'a'
  x = 0 != 'a'

  x = 0 < y
  x = 0 <= y
  x = 0 >= y
  x = 0 > y
  x = 0 == y
  x = 0 != y
  x = true == x
  x = true != x
  x = true & x
  x = false | x

  x = 0 < i()
  x = 0 <= i()
  x = 0 >= i()
  x = 0 > i()
  x = 0 == i()
  x = 0 != i()
  x = true == b()
  x = true != b()
  x = true & b()
  x = false | b()

  x = 0 < a2[0]
  x = 0 <= a2[0]
  x = 0 >= a2[0]
  x = 0 > a2[0]
  x = 0 == a2[0]
  x = 0 != a2[0]
  x = true == a1[0]
  x = true != a1[0]
  x = true & a1[0]
  x = false | a1[0]

  x = 0 < -1
  x = 0 <= -1
  x = 0 >= -1
  x = 0 > -1
  x = 0 == -1
  x = 0 != -1

  x = 0 < 0+0
  x = 0 <= 0+0
  x = 0 >= 0+0
  x = 0 > 0+0
  x = 0 == 0+0
  x = 0 != 0+0

  x = 0 < (0)
  x = 0 <= (0)
  x = 0 >= (0)
  x = 0 > (0)
  x = 0 == (0)
  x = 0 != (0)
  x = true == (false)
  x = true != (false)
  x = true & (false)
  x = false | (false)

  x = 'a' < 1
  x = 'a' <= 1
  x = 'a' >= 1
  x = 'a' > 1
  x = 'a' == 1
  x = 'a' != 1

  x = y < 1
  x = y <= 1
  x = y >= 1
  x = y > 1
  x = y == 1
  x = y != 1
  x = x == false
  x = x != false
  x = x & false
  x = x | true

  x = i() < 1
  x = i() <= 1
  x = i() >= 1
  x = i() > 1
  x = i() == 1
  x = i() != 1
  x = b() == false
  x = b() != false
  x = b() & false
  x = b() | true

  x = a2[0] < 1
  x = a2[0] <= 1
  x = a2[0] >= 1
  x = a2[0] > 1
  x = a2[0] == 1
  x = a2[0] != 1
  x = a1[0] == false
  x = a1[0] != false
  x = a1[0] & false
  x = a1[0] | true

  x = -1 < 1
  x = -1 <= 1
  x = -1 >= 1
  x = -1 > 1
  x = -1 == 1
  x = -1 != 1

  x = 0+0 < 1
  x = 0+0 <= 1
  x = 0+0 >= 1
  x = 0+0 > 1
  x = 0+0 == 1
  x = 0+0 != 1

  x = (0) < 1
  x = (0) <= 1
  x = (0) >= 1
  x = (0) > 1
  x = (0) == 1
  x = (0) != 1
  x = (false) == false
  x = (false) != false
  x = (true) & false
  x = (false) | true
}

b(): bool {
  return false
}

h(x:int[]) {
  x = x + x
  x = x + {2,3}
  x = {1} + x
  x = {1} + {2,3}

  x = x + (x)
  x = x + ({2,3})
  x = {1} + (x)
  x = {1} + ({2,3})

  x = (x) + x
  x = (x) + {2,3}
  x = ({1}) + x
  x = ({1}) + {2,3}
}

