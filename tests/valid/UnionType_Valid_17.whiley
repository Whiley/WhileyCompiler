type IntList is int | int[]

function f(int y) -> int:
    return y

function g(int[] z) -> int[]:
    return z

public export method test() :
    IntList x = 123
    assume f(x) == 123
    x = [1, 2, 3]
    assume g(x) == [1,2,3]
