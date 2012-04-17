import println from whiley.lang.System

void ::main(System.Console sys):
     xs = {1,2,3}
     if 1 ∈ xs:
         sys.out.println(Any.toString(1))
    if 5 in xs:
        sys.out.println(Any.toString(5))
  

