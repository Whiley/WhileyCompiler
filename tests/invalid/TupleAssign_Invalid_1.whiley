
type tac1tup is {int f1, int f2} where f1 < f2

method main(System.Console sys) => void:
    tac1tup x = {f1: 1, f2: 3}
    x.f1 = 2
    assert x.f1 == x.f2
    debug Any.toString(x)
