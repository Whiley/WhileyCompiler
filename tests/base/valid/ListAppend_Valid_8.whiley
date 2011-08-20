import whiley.lang.*:*

[int] append(string input):
    rs = []
    for i in 0..|input|:
        rs = input[i] + rs
    return rs

void System::main([string] args):
    xs = append("abcdefghijklmnopqrstuvwxyz")
    this.out.println(str(xs))
