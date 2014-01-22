import * from whiley.lang.*

method main(System.Console sys) => void:
    xs = {1, 2, 3}
    b = 1.0 & xs
    if b:
        sys.out.println(Any.toString(1))
