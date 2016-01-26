

function g(int x, int y) -> int:
    return x + y

function f1(int x) -> function(int)->(int):
    //
    return &(int y -> g(x, y))

function f2(int y) -> function(int)->(int):
    //
    return &(int x -> g(x, y))

type func is function(int)->(int)

public export method test() :
    func fx = f1(10)
    func fy = f2(20)
    assume fx(1) == 11
    assume fy(1) == 21
