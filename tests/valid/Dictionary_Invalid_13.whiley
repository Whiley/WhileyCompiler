import println from whiley.lang.System

{char=>int} reverse(string input):
    rs = {=>}
    for i in 0..|input|:
        c = input[i]
        rs[c] = i
    return rs

void ::main(System.Console sys):
    xs = reverse("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(Any.toString(xs))
