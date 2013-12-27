import println from whiley.lang.System

void ::main(System.Console sys):
     xs = { 1,2,3,4,5,6,7,8,9,10 }
     ys = { x | x ∈ xs, ((x/2)*2) == x }
     sys.out.println(Any.toString(xs))
     sys.out.println(Any.toString(ys))
