import * from whiley.lang.*

type wmccf7tup is {int y, int x}

function f(System x, int y) => wmccf7tup:
    return {y: x.get(), x: 1}

method get(System this) => int:
    return 1

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(this, 1)))
