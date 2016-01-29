

type cr3nat is (int x) where x < 10

function f(cr3nat x) -> cr3nat:
    return 1

public export method test() :
    int y = f(9)
    assume y == 1
