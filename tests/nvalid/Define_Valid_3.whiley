import println from whiley.lang.System

constant odd is {1, 3, 5}

method main(System.Console sys) => void:
    y = 1
    sys.out.println(Any.toString(y))
