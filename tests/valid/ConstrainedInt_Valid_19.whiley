import whiley.lang.*

type a_nat is (int x) where x >= 0

type b_nat is (int x) where (2 * x) >= x

function f(a_nat x) -> b_nat:
    if x == 0:
        return 1
    else:
        return f(x - 1)

method main(System.Console sys) -> void:
    int x = |sys.args|
    x = f(x)
    sys.out.println(x)
