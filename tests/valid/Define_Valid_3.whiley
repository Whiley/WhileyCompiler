import whiley.lang.System

type odd is (int x) where x in {1, 3, 5}

method main(System.Console sys) -> void:
    odd y = 1
    sys.out.println(y)
