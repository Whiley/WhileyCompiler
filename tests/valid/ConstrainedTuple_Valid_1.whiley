

type nat is (int x) where x >= 0

type tnat is (nat, nat)

function f(tnat tup) -> nat:
    int x, int y = tup
    return x + y

public export method test() -> void:
    (int,int) x = 3, 5
    assume f(x) == 8

