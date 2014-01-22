import * from whiley.lang.*

method main(System.Console sys) => void:
    xs = {1, 2, 3}
    if 1.23 in xs:
        sys.out.println(Any.toString(1))
