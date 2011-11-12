import * from whiley.lang.*

{int} append(string input):
    rs = {}
    for i in 0..|input|:
        rs = rs + {input[i]}
    return rs

void ::main(System sys,[string] args):
    xs = append("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(toString(xs))
