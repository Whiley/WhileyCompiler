

type pos is (int n) where n > 0

type neg is (int n) where n < 0

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
