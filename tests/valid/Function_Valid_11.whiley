import whiley.lang.System

type fr8nat is (int x) where x > 0

type fr8neg is (int x) where x < 0

function f(fr8nat y) => string:
    return "F(NAT)"

function f(fr8neg x) => string:
    return "F(NEG)"

method main(System.Console sys) => void:
    sys.out.println(f(-1))
    sys.out.println(f(1))
