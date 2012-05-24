import println from whiley.lang.System

define listset as string|{int}

{int} f(listset xs):
    return { x | x in xs, ((x/2)*2) == x }

void ::main(System.Console sys):
     xs = { 1,2,3,4,5,6,7,8,9,10 }     
     sys.out.println(f(xs))
     xs = "Hello World"
     sys.out.println(f(xs))
