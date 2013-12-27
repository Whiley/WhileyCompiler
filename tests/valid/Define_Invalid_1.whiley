import println from whiley.lang.System

define point as {int x,int y}
define listint as [int]
define setint as {int}

void ::main(System.Console sys):
     si = {1,2,3}
     li = [1,2,3]     
     p = {x:1,y:2}
     x = p.x     
     sys.out.println(Any.toString(x))
     sys.out.println(Any.toString(|si|))
     sys.out.println(Any.toString(li[0]))
