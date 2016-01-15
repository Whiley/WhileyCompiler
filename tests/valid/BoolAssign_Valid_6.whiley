

function f(int x, int y) -> int:
    bool a = true
    if x < y:
        a = false
    if !a:
        return x + y
    else:
        return 123

public export method test() :
    assume f(1,1) == 123
    assume f(2,1) == 123
    assume f(1,2) == 3
