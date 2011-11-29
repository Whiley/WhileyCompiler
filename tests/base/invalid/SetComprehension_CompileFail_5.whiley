import * from whiley.lang.*

int f({int} xs):
    return |xs|

void ::main(System sys,[string] args):
    xs = {{1},{1,2,3}}
    zs = { {x:x,y:ys} | x∈xs,ys∈x }
    f(zs)
    sys.out.println(toString(xs))
    sys.out.println(toString(zs))
