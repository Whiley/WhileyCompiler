import * from whiley.lang.*

type wmccf6tup is {int y, int x}

function f(System x, int y) => wmccf6tup:
    return {y: x.get(), x: 1}

method get(System this) => int:
    return 1

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(this, 1)))
