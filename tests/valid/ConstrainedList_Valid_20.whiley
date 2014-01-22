import println from whiley.lang.System

type nat is (int x) where x >= 0

function get([nat] ls, int i) => int
requires (i >= 0) && (i <= |ls|)
ensures $ >= 0:
    if i == |ls|:
        return 0
    else:
        return ls[i]

method main(System.Console sys) => void:
    xs = [1, 3, 5, 7, 9, 11]
    c = get(xs, 0)
    sys.out.println(Any.toString(c))
