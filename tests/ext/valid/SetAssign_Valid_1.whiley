import whiley.lang.*:*

// this is a comment!
string f({int} xs) requires |xs| > 0:
    return str(xs)

void ::main(System sys,[string] args):
    ys = {1,2,3}
    zs = ys
    sys.out.println(f(zs))
