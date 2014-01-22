import println from whiley.lang.System

method main(System.Console sys) => void:
    list = [1, 2, 3]
    sublist = list[0..2]
    sys.out.println(Any.toString(list))
    sys.out.println(Any.toString(sublist))
