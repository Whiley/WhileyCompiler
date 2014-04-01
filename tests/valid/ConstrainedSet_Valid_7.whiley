import whiley.lang.System

type pintset is ({int} xs) where |xs| > 1

method main(System.Console sys) => void:
    pintset p = {1, 2}
    sys.out.println(Any.toString(p))
