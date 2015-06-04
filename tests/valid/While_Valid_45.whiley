

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

public export method test():
    //
    assume buildNatSet(-1) == {}
    assume buildNatSet(0) == {}
    assume buildNatSet(1) == {0}
    assume buildNatSet(2) == {0,1}
    assume buildNatSet(3) == {0,1,2}
    assume buildNatSet(4) == {0,1,2,3}
    assume buildNatSet(10) == {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
    
