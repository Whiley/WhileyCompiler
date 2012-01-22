

// this is a comment!
void f({int} xs) requires |xs| > 4:
    debug Any.toString(xs)

void ::main(System.Console sys):
    if |sys.args| > 1:
        ys = {1,2,3}
    else:
        ys = {1,2}
    zs = ys
    f(zs)
