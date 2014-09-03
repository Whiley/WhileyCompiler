import whiley.lang.System

function f({real} ls) => string:
    return Any.toString(ls)

method main(System.Console sys) => void:
    {int} ss = {1,2,3}
    sys.out.println(f(({real}) ss))
