import whiley.lang.System

type pos is (real r) where r > 0.0

type neg is (int n) where n < 0

type expr is pos | neg

function f(expr e) => string:
    if e is pos:
        return "POSITIVE: " ++ Any.toString(e)
    else:
        return "NEGATIVE: " ++ Any.toString(e)

method main(System.Console sys) => void:
    sys.out.println(f(-1))
    sys.out.println(f(1.0))
    sys.out.println(f(1234.0))
