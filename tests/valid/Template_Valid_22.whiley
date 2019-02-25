function map<T,S>(T[] arr, function(T)->(S) fn) -> S[]
requires |arr| > 0:
    //
    S[] marr = [fn(arr[0]);|arr|]
    int i=1
    while i < |arr|:
        //
        marr[i] = fn(arr[i])
        i = i + 1
    //
    return marr

function i2b(int i) -> (bool b):
    return i >= 0

function r2i({int f} rec) -> (int r):
    return rec.f

public export method test():
    //
    int[] ints = [-1,0,1,2,-3]
    {int f}[] recs = [{f:0},{f:5},{f:127}]
    //
    assert map(ints,&i2b) == [false,true,true,true,false]
    assert map(recs,&r2i) == [0,5,127]