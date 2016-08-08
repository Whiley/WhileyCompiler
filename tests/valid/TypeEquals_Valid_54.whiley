function f(int|null x, int|null y) -> (int r)
ensures r == 0 || r == y:
    //
    x = y
    //
    if x is int && x >= 0:
        return x
    else:
        return 0


public export method test():
    assume f(-1,-1)   == 0
    assume f(0,-1)    == 0
    assume f(1,-1)    == 0
    assume f(2,-1)    == 0
    assume f(null,-1) == 0
    
    assume f(-1,0)   == 0
    assume f(0,0)    == 0
    assume f(1,0)    == 0
    assume f(2,0)    == 0
    assume f(null,0) == 0
    
    assume f(-1,1)   == 1
    assume f(0,1)    == 1
    assume f(1,1)    == 1
    assume f(2,1)    == 1
    assume f(null,1) == 1
    
    assume f(-1,2)   == 2
    assume f(0,2)    == 2
    assume f(1,2)    == 2
    assume f(2,2)    == 2
    assume f(null,2) == 2
    
    assume f(-1,null)   == 0
    assume f(0,null)    == 0
    assume f(1,null)    == 0
    assume f(2,null)    == 0
    assume f(null,null) == 0