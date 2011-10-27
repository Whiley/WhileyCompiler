import * from whiley.lang.*

void ::main(System sys,[string] args):
     xs = {1,2,3,4,5,6,7,8,9,10}
     sys.out.println(toString(xs ∩ {2,3,7,11}))
