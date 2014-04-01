import whiley.lang.System

function f({int} xs) => {int}:
    return { -x | x in xs }

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f({1, 2, 3, 4})))
