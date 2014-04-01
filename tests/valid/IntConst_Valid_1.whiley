import whiley.lang.System

method main(System.Console sys) => void:
    int x = 1234567891011121314151617181920
    x = x + 1
    sys.out.println(Any.toString(x))
