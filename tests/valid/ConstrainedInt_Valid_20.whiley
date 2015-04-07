import whiley.lang.*

type a_nat is int

type b_nat is int

function f(a_nat x) -> b_nat:
    if x == 0:
        return 1
    else:
        return f(x - 1)

method main(System.Console sys) -> void:
    int x = |sys.args|
    x = f(x)
    sys.out.println(x)
