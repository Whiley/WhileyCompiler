type fun_t<T> is function(T)->(int)

function inc(int x) -> (int y):
    return x + 1

function map<T>(T[] items, fun_t<T> fn) -> int[]:
    int[] r = [0; |items|]
    //
    for i in 0..|items| where |r| == |items|:
        r[i] = fn(items[i])
    //
    return r

public export method test():
    (int|bool)[] items = [1,2,true,false]
    // Following should not compile!
    assume map(items,&inc) == [2,3,4]
