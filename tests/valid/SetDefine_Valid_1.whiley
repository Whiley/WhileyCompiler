import println from whiley.lang.System

type pintset is {int} where |$| > 1

method main(System.Console sys) => void:
    p = {1, 2}
    sys.out.println(Any.toString(p))
