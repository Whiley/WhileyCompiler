import * from whiley.lang.*

void ::main(System sys,[string] args):
     xs = { 1,2,3,4 }
     ys = { 1,2 }
     zs = { x+y | x∈xs, y∈ys }
     sys.out.println(Any.toString(xs))
     sys.out.println(Any.toString(ys))
     sys.out.println(Any.toString(zs))

