
type i8 is int x where (x >= -128) && (x <= 127)

function g(int x) -> (int r)
ensures (r > 0) && (r <= 256):
    //
    if x <= 0:
        return 1
    else:
        return x

function f(int x) -> [i8]:
    return [g(x)]
