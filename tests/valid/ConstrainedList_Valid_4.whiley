type i8 is (int n) where -128 <= n && n <= 127

function g(int x) -> (int r)
ensures (r > 0) && (r < 125):
    return 1

function f(int x) -> i8[]:
    return [g(x)]

public export method test() :
    int[] bytes = f(0)
    assume bytes == [1]
