import println from whiley.lang.System

void ::main(System.Console sys):
     a1 = [[1,2,3],[0]]
     a2 = a1
     a2[0] = [3,4,5]
     
     sys.out.println(Any.toString(a1[0]))
     sys.out.println(Any.toString(a1[1]))
     sys.out.println(Any.toString(a2[0]))
     sys.out.println(Any.toString(a2[1]))
