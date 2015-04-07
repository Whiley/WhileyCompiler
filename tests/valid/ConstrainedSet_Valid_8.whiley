import whiley.lang.*

type pintset is {int}

method main(System.Console sys) -> void:
    pintset p = {1, 2}
    sys.out.println(p)
