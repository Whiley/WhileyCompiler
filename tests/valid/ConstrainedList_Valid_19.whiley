

type nat is (int x) where x >= 0

function g(nat[] xs) -> nat[]:
    return xs

function f(nat[] xs) -> nat[]:
    return g(xs)

public export method test() :
    nat[] rs = f([1, 2, 3])
    assume rs == [1,2,3]
