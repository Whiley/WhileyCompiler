import whiley.lang.*

type nat is (int x) where x >= 0

function get([nat] ls, int i) -> (int r)
requires (i >= 0) && (i <= |ls|)
ensures r >= 0:
    //
    if i == |ls|:
        return 0
    else:
        return ls[i]

method main(System.Console sys) -> void:
    [int] xs = [1, 3, 5, 7, 9, 11]
    int c = get(xs, 0)
    sys.out.println(c)
