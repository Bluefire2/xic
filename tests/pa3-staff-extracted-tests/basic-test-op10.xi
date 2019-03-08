f() {
  x: int[] = {47}
  x01: bool = {0} == x
  x02: bool = {0} != x
  x03: bool = x == {1}
  x04: bool = x != {1}

  x05: bool = {{true}} == {{false}}
  x06: bool = {{true}} != {{false}}
  x07: bool = {""} == {"hello"}
  x08: bool = {""} != {"hello"}

  x09: bool = "" == x
  x10: bool = "" != x
  x11: bool = x == "hello"
  x12: bool = x != "hello"

  x13: bool = {{{0}}, {{1}}, {{2}}} == {{{3}}, {{4}}, {{5}}}
  x14: bool = {{{0}}, {{1}}, {{2}}} != {{{3}}, {{4}}, {{5}}}
}

