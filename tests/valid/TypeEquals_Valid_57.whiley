type nat is (int x) where x >= 0

function f(int|null x) -> nat:
    //
    if x is null || x < 0:
        return 0
    else:
        return (nat) x

public export method test():
    assume f(null) == 0
    assume f(-1) == 0
    assume f(0) == 0
    assume f(1) == 1
    assume f(2) == 2
