import * from whiley.lang.*

void ::main(System sys,[string] args):
     xs = {1,2,3,4}
     ys = xs ∪ {5,1}
     sys.out.println(str(xs))
     xs = xs ∪ {6}
     sys.out.println(str(xs))
     sys.out.println(str(ys))
