import whiley.lang.System

function append(string input) => {int}:
    {int} rs = {}
    for i in 0 .. |input|:
        rs = rs + {(int) input[i]}
    return rs

method main(System.Console sys) => void:
    {int} xs = append("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(Any.toString(xs))
