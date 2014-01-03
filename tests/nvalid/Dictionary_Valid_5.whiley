import println from whiley.lang.System

function reverse(string input) => {char=>int}:
    rs = {=>}
    for i in 0 .. |input|:
        c = input[i]
        rs[c] = i
    return rs

method main(System.Console sys) => void:
    xs = reverse("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(Any.toString(xs))
