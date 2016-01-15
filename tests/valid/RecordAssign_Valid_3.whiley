

type tac1tup is {int f1, int f2}

function f() -> tac1tup:
    return {f1: 2, f2: 3}

public export method test() :
    tac1tup x = f()
    x.f1 = x.f2 - 2
    assert x.f1 != x.f2
    assume x == {f1: 1, f2: 3}
