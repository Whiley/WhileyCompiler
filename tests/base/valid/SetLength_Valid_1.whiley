import * from whiley.lang.*

void ::main(System.Console sys,[string] args):
     xs = {1,2,3}
     sys.out.println(Any.toString(|xs|))
