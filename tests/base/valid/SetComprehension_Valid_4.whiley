import whiley.lang.*:*

{int} f([int] xs):
    return { x | x in xs, x > 1 }

void ::main(System sys,[string] args):
    sys.out.println(str(f([1,2,3])))
    sys.out.println(str(f([1,2,3,3])))
    sys.out.println(str(f([-1,1,2,-1,3,3])))
