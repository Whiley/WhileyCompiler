function f(int|null x) -> int:
    return 0

function g(function(int)->int func) -> int:
    return func(1)

public export method test() :
    assume g(&f) == 0
