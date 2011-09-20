import * from whiley.lang.*

void ::main(System sys,[string] args):
     xs = { 1,2,3,4 }
     ys = { 1,2 }
     zs = { x+y | x∈xs, y∈ys }
     sys.out.println(str(xs))
     sys.out.println(str(ys))
     sys.out.println(str(zs))

