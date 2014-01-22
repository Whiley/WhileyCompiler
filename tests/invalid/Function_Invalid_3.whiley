import * from whiley.lang.*

method f(System this, int x) => void:
    sys.out.println(Any.toString(x))

method main(System.Console sys) => void:
    f(1)
