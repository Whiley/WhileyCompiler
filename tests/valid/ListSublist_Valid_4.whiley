import whiley.lang.System

method main(System.Console sys) => void:
    [int] list = [1, 2, 3]
    [int] sublist = list[0..2]
    sys.out.println(Any.toString(list))
    sys.out.println(Any.toString(sublist))
