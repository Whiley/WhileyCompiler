

function f(real x) -> real:
    return x + 1

function g(function(int)->real func) -> real:
    return func(1)

public export method test() :
    assume g(&f) == 2.0
