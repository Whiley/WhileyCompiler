type map is int[]

method checkAll(&map m) -> bool:
    return all { i in 0..|*m| | (*m)[i] >= 0 }

method checkSome(&map m) -> bool:
    return some { i in 0..|*m| | (*m)[i] >= 0 }

public export method test():
    bool a0 = checkAll(new [])
    bool a1 = checkAll(new [1])
    bool a2 = checkAll(new [-1])
    bool a3 = checkAll(new [2,3])
    bool a4 = checkAll(new [2,3,-1])
    //
    assume a0
    assume a1
    assume !a2
    assume a3
    assume !a4
    //
    bool s0 = checkSome(new [])
    bool s1 = checkSome(new [1])
    bool s2 = checkSome(new [-1])
    bool s3 = checkSome(new [2,3])
    bool s4 = checkSome(new [2,3,-1])    
    //
    assume !s0
    assume s1
    assume !s2
    assume s3
    assume s4
    
    