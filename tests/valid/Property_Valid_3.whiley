property nat(int x) -> (bool r):
    return x >= 0

property natArray(int[] xs) -> (bool r):
   return all { i in 0..|xs| | nat(xs[i]) }

function id(int[] xs) -> (int[] ys)
requires natArray(xs)
ensures natArray(ys):
    return xs


public export method test():
    assume id([0]) == [0]
    assume id([1,2]) == [1,2]
    assume id([1,2,3]) == [1,2,3]
