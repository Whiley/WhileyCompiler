import whiley.lang.System

function append(ASCII.string input) -> [ASCII.char]:
    [ASCII.char] rs = []
    for i in 0 .. |input|:
        rs = [input[i]] ++ rs
    return rs

method main(System.Console sys) -> void:
    [ASCII.char] xs = append("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(Any.toString(xs))
