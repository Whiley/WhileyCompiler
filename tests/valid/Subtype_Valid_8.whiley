import whiley.lang.System

type sr4set is ({int} xs) where |xs| > 0

method main(System.Console sys) => void:
    x = {1}
    sys.out.println(Any.toString(x))
