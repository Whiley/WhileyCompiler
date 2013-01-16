import println from whiley.lang.System

[int] f([int] x) requires x[0] == 0:
   assert x[0] == 0
   return x

[int] g([int] x) requires x[0] == 0:
   assert |x| > 0
   return x

void ::main(System.Console console):
    console.out.println(f([0,1,2]))
    console.out.println(g([0]))
    console.out.println(g([0,1,2]))








    
    
