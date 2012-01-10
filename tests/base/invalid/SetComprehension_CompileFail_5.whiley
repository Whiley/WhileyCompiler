

int f({int} xs):
    return |xs|

void ::main(System.Console sys):
    xs = {{1},{1,2,3}}
    zs = { {x:x,y:ys} | x∈xs,ys∈x }
    f(zs)
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(zs))
