

type a_nat is int

type b_nat is int

function f(a_nat x) -> b_nat:
    if x == 0:
        return 1
    else:
        return f(x - 1)

public export method test() :
    int x = 0
    x = f(x)
    assume x == 1
