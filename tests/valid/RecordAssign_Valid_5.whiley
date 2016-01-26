

type tac2ta is {int f1, int f2}

type tac2tb is {int f1, int f2}

function f(tac2ta x) -> tac2tb:
    return {f1: x.f1 - 1, f2: x.f2}

public export method test() :
    tac2ta x = {f1: 2, f2: 3}
    assume x == {f1: 2, f2: 3}
    x.f1 = 1
    tac2tb y = f(x)
    assume y == {f1: 0, f2: 3}
