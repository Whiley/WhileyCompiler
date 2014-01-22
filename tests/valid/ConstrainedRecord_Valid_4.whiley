import println from whiley.lang.System

type point is {int y, int x} where ($.x > 0) && ($.y > 0)

method main(System.Console sys) => void:
    p = {y: 1, x: 1}
    sys.out.println(Any.toString(p))
