type nat is (int n) where n >= 0

type pair is {int first, nat second}

function get(pair p) -> (int f, int s):
    return p.first, p.second

function min(pair p) -> int:
    int x
    int y
    x,y = get(p)
    if x > y:
        return y
    else:
        return x

method main() -> int:
    return min({first: 1, second: -1})
