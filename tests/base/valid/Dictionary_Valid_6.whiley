{int} reverse(string input):
    rs = {}
    for i in 0..|input|:
        c = input[i]
        rs[c] = i
    return rs

void System::main([string] args):
    xs = reverse("abcdefghijklmnopqrstuvwxyz")
    out.println(str(xs))
