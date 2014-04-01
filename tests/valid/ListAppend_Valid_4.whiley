import whiley.lang.System

function append(string input) => [char]:
    [char] rs = []
    for i in 0 .. |input|:
        rs = [input[i]] ++ rs
    return rs

method main(System.Console sys) => void:
    [char] xs = append("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(Any.toString(xs))
