import whiley.lang.*:*

// this is a comment!
void f({int} xs) requires |xs| > 4:
    debug str(xs)

void ::main(System sys,[string] args):
    if |args| > 1:
        ys = {1,2,3}
    else:
        ys = {1,2}
    zs = ys
    f(zs)
