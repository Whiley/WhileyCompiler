import whiley.lang.System

type wmcr6tup is {int y, int x}

method get() => int:
    return 1

method f(int y) => wmcr6tup:
    return {y: get(), x: y}

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(1)))
