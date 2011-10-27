import * from whiley.lang.*

void ::main(System sys,[string] args):
     xs = { 1,2,3,4 }
     ys = { 1,2 }
     zs = { x+y | x∈xs, y∈ys, x!=y }
     sys.out.println(toString(xs))
     sys.out.println(toString(ys))
     sys.out.println(toString(zs))
