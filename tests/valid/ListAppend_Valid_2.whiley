import whiley.lang.System

function append(string input) => [char]:
    rs = []
    for i in 0 .. |input|:
        rs = rs ++ [input[i]]
    return rs

method main(System.Console sys) => void:
    xs = append("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(Any.toString(xs))
