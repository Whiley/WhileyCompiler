

function f(real[] ls) -> real[]:
    return ls

public export method test() :
    int[] ls = [1,2,3]
    assume f((real[]) ls) == [1.0,2.0,3.0]
