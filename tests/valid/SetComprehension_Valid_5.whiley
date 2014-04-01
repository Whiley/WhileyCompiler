import whiley.lang.System

type dictset is {int=>int} | {int}

function f(dictset xs) => {int | (int, int)}:
    return { x | x in xs, x is int }

method main(System.Console sys) => void:
    dictset xs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
    sys.out.println(f(xs))
    xs = {1=>2, 2=>3}
    sys.out.println(f(xs))
