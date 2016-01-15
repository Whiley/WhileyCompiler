

type nat is (int x) where x >= 0

function max(int a, int b) -> (int r)
ensures (r == a) || (r == b)
ensures (a <= r) && (b <= r):
    //
    if a < b:
        return b
    else:
        return a

function diff(int a, int b) -> (nat r)
ensures r == max(a - b, b - a):
    int diff
    //
    if a > b:
        diff = a - b
    else:
        diff = b - a
    //
    return diff

public export method test() :
    int i = 0
    while i < 20:
        int j = 0
        while j < 20:
            assume i < j || diff(i-10,j-10) == (i - j)
            assume i > j || diff(i-10,j-10) == (j - i)
            j = j + 1
        //
        i = i + 1
    //
