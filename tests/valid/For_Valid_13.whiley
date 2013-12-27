import println from whiley.lang.System

// non-deterministically select an element from a set
int select({int} xs) 
    requires |xs| > 0, 
    ensures $ in xs:
    //
    for x in xs:
        return x
    // following line should be deadcode
    return 0

void ::main(System.Console sys):
    sys.out.println(select({1,2,3,4,5,6,7,8,9,10}))

