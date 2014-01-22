import println from whiley.lang.System

type listset is [int] | {int}

function f(listset xs) => {int}:
    return { x | x in xs, ((x / 2) * 2) == x }

method main(System.Console sys) => void:
    xs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
    sys.out.println(f(xs))
    xs = [1, 2, 3, 4, 5, 6, 7]
    sys.out.println(f(xs))
