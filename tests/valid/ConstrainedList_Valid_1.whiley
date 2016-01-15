type u8 is (int n) where 0 <= n && n <= 255

function f(int x) -> u8[]
requires x == 0 || x == 169:
    return [x]

public export method test() :
    u8[] bytes = f(0)
    assume bytes == [0]
