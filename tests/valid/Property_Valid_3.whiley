property nat(int x) where x >= 0

property natArray(int[] xs)
where all { i in 0..|xs| | nat(xs[i]) }

function id(int[] xs) -> (int[] ys)
requires natArray(xs)
ensures natArray(ys):
    return xs


public export method test():
    assume id([0]) == [0]
    assume id([1,2]) == [1,2]
    assume id([1,2,3]) == [1,2,3]
