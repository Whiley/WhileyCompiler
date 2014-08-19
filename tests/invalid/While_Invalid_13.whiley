
type nat is (int n) where n >= 0

function extract(int i, [int] ls) => int
requires i >= 0:
    //
    int r = 0
    //
    while i < |ls|:
        r = r + ls[i]
        i = i - 1
    //
    return r

method main(System.Console sys) => void:
    int r = extract(0, [-2, -3, 1, 2, -23, 3, 2345, 4, 5])
    debug Any.toString(r)

