import whiley.lang.*

type sr4set is {int}

method main(System.Console sys) -> void:
    sr4set x = {1}
    sys.out.println(x)
