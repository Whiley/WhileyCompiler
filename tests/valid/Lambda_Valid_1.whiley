

type func is function(int) -> int

function g() -> func:
    return &(int x -> x + 1)

public export method test() :
    func f = g()
    assume f(1) == 2
    assume f(2) == 3
    assume f(3) == 4
