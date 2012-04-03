import println from whiley.lang.System

define dictset as {int=>int}|{int}

{int|(int,int)} f(dictset xs):
    return { x | x in xs, x is int }

void ::main(System.Console sys):
     xs = { 1,2,3,4,5,6,7,8,9,10 }     
     sys.out.println(f(xs))
     xs = {1=>2, 2=>3}
     sys.out.println(f(xs))
