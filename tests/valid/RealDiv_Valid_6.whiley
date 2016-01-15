

function g(real x) -> real:
    return x / 3.0

public export method test() :
    assume g(0.234) == (0.468/6.0)
