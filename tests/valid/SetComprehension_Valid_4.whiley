import whiley.lang.System

type listset is string | {int}

function f(listset xs) => {int}:
    return { x | x in xs, ((x / 2) * 2) == x }

method main(System.Console sys) => void:
    listset xs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
    sys.out.println(f(xs))
    xs = "Hello World"
    sys.out.println(f(xs))
