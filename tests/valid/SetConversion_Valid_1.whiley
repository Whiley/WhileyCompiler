

function f({real} ls) -> real:
    real r = 0.0
    for x in ls:
        r = r + x
    return r

public export method test() -> void:
    {int} ss = {1,2,3}
    assume f(({real}) ss) == 6.0
