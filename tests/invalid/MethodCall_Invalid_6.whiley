import * from whiley.lang.*

type wmccf6tup is {int y, int x}

function f(System.Console x, int y) -> wmccf6tup:
    return {y: get(x), x: 1}

method get(System.Console this) -> int:
    return 1

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(f(sys, 1)))
