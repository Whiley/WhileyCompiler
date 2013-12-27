import println from whiley.lang.System

[int] f():
    return [1,2]

void ::main(System.Console sys):
     a1 = f()
     a2 = f()
     a2[0] = 0
     
     sys.out.println(Any.toString(a1[0]))
     sys.out.println(Any.toString(a1[1]))
     sys.out.println(Any.toString(a2[0]))
     sys.out.println(Any.toString(a2[1]))
