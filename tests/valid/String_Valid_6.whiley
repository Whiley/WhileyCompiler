
function append(int[] xs, int[] ys) -> (int[] zs)
ensures |zs| == |xs| + |ys|:
    //    
    int count = |xs| + |ys|
    int[] rs = [0; count]
    //
    int i = 0
    while i < |xs|
        where i >= 0 && i <= |xs|
        where |xs| + |ys| == |rs|:
        rs[i] = xs[i]
        i = i + 1
    //
    int j = 0
    while j < |ys|
        where j >= 0
        where |xs| + |ys| == |rs|:
        rs[j + i] = ys[j]
        j = j + 1
    //
    return rs
    
public export method test() :
    assume append("Hello ","122") == "Hello 122"
