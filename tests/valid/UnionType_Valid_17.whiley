type IntList is int | int[]

method f(int y) -> int:
    return y

method g(int[] z) -> int[]:
    return z

public export method test() :
    IntList x = 123
    if x is int:
        assume f(x) == 123
    x = [1, 2, 3]
    if x is int[]:
        assume g(x) == [1,2,3]
