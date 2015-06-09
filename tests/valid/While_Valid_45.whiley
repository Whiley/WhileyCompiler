

function buildNatList(int n) -> ([int] m):
    //
    int i = 0
    [int] rs = []
    //
    // forall(int i):
    //    i == 0 ==> i >= 0
    // forall(int r, {int} rs):
    //    r in rs ==> r >= 0
    while i < n
        where i >= 0
        where all { r in rs | r >= 0 }:
        //
        rs = rs ++ [i]
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
    assume buildNatList(-1) == []
    assume buildNatList(0) == []
    assume buildNatList(1) == [0]
    assume buildNatList(2) == [0,1]
    assume buildNatList(3) == [0,1,2]
    assume buildNatList(4) == [0,1,2,3]
    assume buildNatList(10) == [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
    
