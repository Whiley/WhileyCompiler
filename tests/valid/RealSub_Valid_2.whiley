

function f(real x) -> real:
    return 0.0 - x

public export method test() :
    assume f(1.234) == -1.234
