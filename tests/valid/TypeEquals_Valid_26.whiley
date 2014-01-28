import whiley.lang.System

type T is {int} | int

function f(T x) => int:
    if x is {int}:
        return |x|
    else:
        return x

public method main(System.Console sys) => void:
    sys.out.println("RESULT: " ++ Any.toString(f({1, 2, 3, 4})))
    sys.out.println("RESULT: " ++ Any.toString(f(123)))
