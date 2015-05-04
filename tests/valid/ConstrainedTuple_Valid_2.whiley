
type nat is (int x) where x >= 0

type tup is (int, int)

function f(tup t) -> int:
    if t is (nat,nat):
        nat x, nat y = t
        return x + y
    //
    return 0

public export method test() -> void:
    (int,int) x = 3, 5
    assume f(x) == 8
    x = -3, 5
    assume f(x) == 0
    x = 3, -5
    assume f(x) == 0
