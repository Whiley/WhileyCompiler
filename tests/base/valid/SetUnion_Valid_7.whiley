import * from whiley.lang.*

{int} append(string input):
    rs = {}
    for i in 0..|input|:
        rs = rs + {input[i]}
    return rs

void ::main(System.Console sys,[string] args):
    xs = append("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(Any.toString(xs))
