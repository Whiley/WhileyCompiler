import whiley.lang.System

constant ITEMS is [-1, 2, 3]

method main(System.Console sys) => void:
    sys.out.println(Any.toString(ITEMS))
