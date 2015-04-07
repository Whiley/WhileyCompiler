import whiley.lang.System

function buildNatSet(int n) -> ({int} m):
    //
    int i = 0
    {int} rs = {}
    //
    // forall(int i):
    //    i == 0 ==> i >= 0
    // forall(int r, {int} rs):
    //    r in rs ==> r >= 0
    while i < n
        where i >= 0
        where all { r in rs | r >= 0 }:
        //
        rs = rs + {i}
        i = i + 1
        // forall(int i):
        //    i >= 0 ==> i+1 >= 0
        // forall({int} rs):
        //    if:
        //      forall(int r):
        //         r in rs ==> r >= 0
        //    then:
        //      forall(int r):
        //         r in rs+{i} ==> r >= 0
    //
    return rs

method main(System.Console console):
    //
    {int} nset = buildNatSet(10)
    console.out.println(nset)
    
