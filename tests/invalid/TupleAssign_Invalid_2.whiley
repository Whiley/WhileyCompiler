
type tac2ta is {int f1, int f2} where f1 < f2

type tac2tb is {int f1, int f2} where (f1 + 1) < f2

function f(tac2tb y) => tac2tb:
    return y

method main(System.Console sys) => void:
    tac2ta x = {f1: 1, f2: 3}
    debug Any.toString(x)
    x.f1 = 2
    debug Any.toString(f(x))
