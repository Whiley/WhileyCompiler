import println from whiley.lang.System

void ::main(System.Console sys):
  aset = {1, 21, 3, 4, 1}
  bset = {2, 2, 3, 4, 9}
  cset = {-1.0, 25, true, 7, 13.4, false}
  sys.out.println(aset + bset)
  sys.out.println(aset & bset)
  sys.out.println(cset + aset)
  sys.out.println(cset & aset)