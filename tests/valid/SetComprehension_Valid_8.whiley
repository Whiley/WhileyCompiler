import whiley.lang.System

function f([int] xs) => {int}:
    return { x | x in xs, x > 1 }

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f([1, 2, 3])))
    sys.out.println(Any.toString(f([1, 2, 3, 3])))
    sys.out.println(Any.toString(f([-1, 1, 2, -1, 3, 3])))
