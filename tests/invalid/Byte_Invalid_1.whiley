import * from whiley.lang.*

method main(System.Console sys) -> void:
    byte b = 00000001b
    int i = b * 2
    sys.out.println(Any.toString(i))
