

function f(int x) -> int:
    return x * 12

function g(function(int)->real func) -> real:
    return func(1)

public export method test() :
    assume g((function(int)->real) &f) == 12.0
