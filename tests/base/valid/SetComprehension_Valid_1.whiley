import * from whiley.lang.*

void ::main(System sys,[string] args):
     xs = { 1,2,3,4,5,6,7,8,9,10 }
     ys = { x | x ∈ xs, ((x/2)*2) == x }
     sys.out.println(toString(xs))
     sys.out.println(toString(ys))
