import whiley.lang.System

type fr3nat is int

function f(int x) => string:
    return Any.toString(x)

method main(System.Console sys) => void:
    int y = 234987234987234982304980130982398723
    sys.out.println(f(y))
