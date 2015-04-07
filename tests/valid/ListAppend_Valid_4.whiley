import whiley.lang.*

function append([int] input) -> [int]:
    [int] rs = []
    for i in 0 .. |input|:
        rs = [input[i]] ++ rs
    return rs

method main(System.Console sys) -> void:
    [int] xs = append("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(xs)
