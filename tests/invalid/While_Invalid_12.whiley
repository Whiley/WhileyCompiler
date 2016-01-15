
type nat is (int x) where x > 0

function f(int v) -> (int r)
ensures r >= 0:
    //
    int i = 0
    while i < 100 where i >= 0:
        i = i - 1
        if i == v:
            break
        i = i + 2
    //
    return i
