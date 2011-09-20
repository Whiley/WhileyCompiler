import * from whiley.lang.*

[int] f():
    return [1,2]

void ::main(System sys,[string] args):
     a1 = f()
     a2 = f()
     a2[0] = 0
     
     sys.out.println(str(a1[0]))
     sys.out.println(str(a1[1]))
     sys.out.println(str(a2[0]))
     sys.out.println(str(a2[1]))
