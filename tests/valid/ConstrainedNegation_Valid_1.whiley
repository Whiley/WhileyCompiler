import println from whiley.lang.System

type nat is (int x) where x >= 0

type neg is !nat

function f(neg x) => int
ensures $ < 0:
    return x

public method main(System.Console sys) => void:
    sys.out.println(f(-1))
    sys.out.println(f(-2))
