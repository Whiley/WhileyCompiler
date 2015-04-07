import whiley.lang.*

function f({real} ls) -> real:
    real r = 0.0
    for x in ls:
        r = r + x
    return r

method main(System.Console sys) -> void:
    {int} ss = {1,2,3}
    sys.out.println(f(({real}) ss))
