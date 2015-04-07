import whiley.lang.*

type pos is (int x) where x > 0

type neg is (int x) where x < 0

type posneg is pos | neg

function isPosNeg(any v) -> bool:
    if v is posneg:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    sys.out.println(isPosNeg(1))
    sys.out.println(isPosNeg(0))
    sys.out.println(isPosNeg(-1))
