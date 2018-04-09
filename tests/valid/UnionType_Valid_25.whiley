
type nintarr is bool[]|int[]

public export method test() :
    nintarr xs = [1,2,3]
    //
    if xs is int[]:
        xs[0] = 0
    //
    assume xs == [0,2,3]
