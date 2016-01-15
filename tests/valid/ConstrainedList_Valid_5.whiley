

function g(int x) -> int:
    if (x <= 0) || (x >= 125):
        return 1
    else:
        return x

function f(int x) -> int[]:
    return [g(x)]

public export method test() :
    int[] bytes = f(0)
    assume bytes == [1]
