import whiley.lang.System

type ur4nat is int

type tur4nat is int

type wur4nat is ur4nat | tur4nat

function f(wur4nat x) => string:
    return Any.toString(x)

method main(System.Console sys) => void:
    sys.out.println(f(1))
