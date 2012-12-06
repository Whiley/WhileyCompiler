import println from whiley.lang.System

// this is a comment!
string f({int} xs):
    return Any.toString(xs)

void ::main(System.Console sys):
     // the following assignment needs to be long enough to exceed
     // the size of implementation chunks
    ys = {1,2,3,4,5,6,7,8,9}
    zs = ys
    sys.out.println(f(zs))
