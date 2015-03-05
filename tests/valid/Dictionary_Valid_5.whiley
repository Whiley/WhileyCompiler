import whiley.lang.System

function reverse(ASCII.string input) -> {ASCII.char=>int}:
    {ASCII.char=>int} rs = {=>}
    for i in 0 .. |input|:
        ASCII.char c = input[i]
        rs[c] = i
    return rs

method main(System.Console sys) -> void:
    {ASCII.char=>int} xs = reverse("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(Any.toString(xs))
