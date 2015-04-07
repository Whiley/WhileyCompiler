import whiley.lang.*

function reverse([int] input) -> {int=>int}:
    {int=>int} rs = {=>}
    for i in 0 .. |input|:
        int c = input[i]
        rs[c] = i
    return rs

method main(System.Console sys) -> void:
    {int=>int} xs = reverse("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(xs)
