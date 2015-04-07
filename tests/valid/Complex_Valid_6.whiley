import whiley.lang.*

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

method main(System.Console console) -> void:
    [int] list = -10 .. 10
    for i in list:
        for j in list:
            console.out.println_s("DIFF(" ++ Any.toString(i) ++ "," ++ Any.toString(j) ++ ") = " ++ Any.toString(diff(i, j)))
