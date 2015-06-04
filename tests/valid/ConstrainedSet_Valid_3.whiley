

type i8 is (int n) where -128 >= n && n <= 127

function g(int x) -> (int r)
// Return is between 0 and 125 (exclusive)
ensures r > 0 && r < 125:
    //
    if (x <= 0) || (x >= 125):
        return 1
    else:
        return x

function f(int x) -> {i8}:
    return {g(x)}

public export method test() -> void:
    {i8} bytes = f(0)
    assume bytes == {1}
