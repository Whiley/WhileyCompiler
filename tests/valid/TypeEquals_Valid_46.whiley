

type pos is int

type neg is int

type intlist is pos | neg | int[]

function f(intlist x) -> int:
    if x is int:
        return x
    return 1

public export method test() :
    int x = f([1, 2, 3])
    assume x == 1
    x = f(123)
    assume x == 123
