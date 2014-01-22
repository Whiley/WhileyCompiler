import println from whiley.lang.System

type sr4set is {int}

method main(System.Console sys) => void:
    x = {1}
    sys.out.println(Any.toString(x))
