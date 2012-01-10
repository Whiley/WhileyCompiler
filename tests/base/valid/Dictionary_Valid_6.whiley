import * from whiley.lang.*

{char=>int} reverse(string input):
    rs = {=>}
    for i in 0..|input|:
        c = input[i]
        rs[c] = i
    return rs

void ::main(System.Console sys,[string] args):
    xs = reverse("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(Any.toString(xs))
