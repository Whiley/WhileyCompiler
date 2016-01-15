

type wierd is (int[] xs) where some { i in 0..|xs| | xs[i] > 0 }

function f(int[] xs) -> wierd
requires |xs| > 0:
    xs[0] = 1
    return xs

public export method test() :
    assume f([-1, -2]) == [1,-2]
