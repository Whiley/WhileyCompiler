

type func is function(int) -> int

function g(int y) -> func:
    return &(int x -> x + y)

public export method test() :
    func f = g(3)
    assume f(1) == 4
    assume f(2) == 5
    assume f(3) == 6
    f = g(19)
    assume f(1) == 20
    assume f(2) == 21
    assume f(3) == 22
