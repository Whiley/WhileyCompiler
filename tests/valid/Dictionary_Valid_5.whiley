import whiley.lang.System

function reverse(string input) => {char=>int}:
    {char=>int} rs = {=>}
    for i in 0 .. |input|:
        char c = input[i]
        rs[c] = i
    return rs

method main(System.Console sys) => void:
    {char=>int} xs = reverse("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(Any.toString(xs))
