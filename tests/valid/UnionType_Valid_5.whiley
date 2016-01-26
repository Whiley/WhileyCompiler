

type ur4nat is (int x) where x > 0

type tur4nat is (int x) where x > 10

type wur4nat is ur4nat | tur4nat

function f(wur4nat x) -> any:
    return x

public export method test() :
    assume f(1) == 1
