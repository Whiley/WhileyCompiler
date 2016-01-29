

type cr1nat is (int x) where x < 10

function f(cr1nat x) -> int:
    int y = x
    return y

public export method test() :
    assume f(9) == 9
