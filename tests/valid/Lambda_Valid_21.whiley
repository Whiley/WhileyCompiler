type fun_t<T> is function(T)->(int)

function inc(int x) -> (int y):
    return x + 1

function to_nat(int|bool x) -> (int y):
    if x is int:
        return x
    else if x:
        return 1
    else:
        return 0

function map<T>(T[] items, fun_t<T> fn) -> int[]:
    int[] r = [0; |items|]
    //
    for i in 0..|items| where |r| == |items|:
        r[i] = fn(items[i])
    //
    return r

public export method test():
    //
    assume map([1,2,3],&inc) == [2,3,4]
    // following could coerce [1,2,3] into int|bool[]
    assume map([1,2,3],&to_nat) == [1,2,3]
    // prevent coercion
    int[] xs = [4,5,6]
    assume map(xs,&to_nat) == [4,5,6]