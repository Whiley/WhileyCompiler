import whiley.lang.*:*

{int->char} reverse(string input):
    rs = {}
    for i in 0..|input|:
        c = input[i]
        rs[c] = i
    return rs

void ::main(System sys,[string] args):
    xs = reverse("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(str(xs))
