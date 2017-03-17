property nat(int x) where x >= 0

property natArray(int[] xs)
where all { i in 0..|xs| | nat(xs[i]) }

function id(int[] xs) -> (int[] ys)
ensures natArray(ys):
    return xs
