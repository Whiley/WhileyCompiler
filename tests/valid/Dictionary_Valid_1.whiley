import whiley.lang.System

method main(System.Console sys) => void:
    x = 1
    map = {1=>x, 3=>2}
    sys.out.println(Any.toString(map))
