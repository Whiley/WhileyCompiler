import println from whiley.lang.System

method main(System.Console sys) => void:
    x = (2 * 3) + 1
    sys.out.println(Any.toString(x))
