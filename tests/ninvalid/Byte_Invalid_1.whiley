import * from whiley.lang.*

method main(System.Console sys) => void:
    b = 00000001b
    i = b * 2
    sys.out.println(Any.toString(i))
