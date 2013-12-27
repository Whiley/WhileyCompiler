import println from whiley.lang.System

void ::main(System.Console sys):
     xs = { 1,2,3,4 }
     ys = { 1,2 }
     zs = { x+y | x∈xs, y∈ys, x!=y }
     sys.out.println(Any.toString(xs))
     sys.out.println(Any.toString(ys))
     sys.out.println(Any.toString(zs))
