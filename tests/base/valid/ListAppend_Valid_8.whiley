import println from whiley.lang.System

[char] append(string input):
    rs = []
    for i in 0..|input|:
        rs = [input[i]] + rs
    return rs

void ::main(System.Console sys):
    xs = append("abcdefghijklmnopqrstuvwxyz")
    sys.out.println(Any.toString(xs))
