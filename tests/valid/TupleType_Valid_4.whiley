

type Tup1 is (int, int)

type Tup2 is (real, real)

function f(Tup1 x) -> Tup2:
    return (Tup2) x

public export method test() -> void:
    Tup2 x = f((1, 2))
    assume x == (1.0,2.0)
