type nat is (int x) where x >= 0
type l10 is (int x) where x < 10

function f(int v) -> (int r)
ensures r >= 0:
    //
    if v is nat&l10:
        return 1
    //
    return 0

public export method test():
    assume f(1) == 1
    assume f(9) == 1
    assume f(10) == 0
    assume f(-1) == 0
