function permute(int[] xs) -> (int[] rs, int[] witness)
ensures |rs| == |witness|
ensures |xs| == |witness|
ensures all { k in 0..|witness| | xs[k] == rs[witness[k]] }:
    //
    int i = 0
    int[] ws = [0; |xs|] // ghost "witness"
    //
    while i < |xs|
    where i >= 0 && |xs| == |ws|
    where all { j in 0..i | xs[j] == xs[ws[j]] }:
        ws[i] = i
        i = i + 1
    //
    return xs,ws

public export method test():
    int[] ys
    int[] ws
    //
    (ys,ws) = permute([1,2,3])
    assume ys == [1,2,3] && ws == [0,1,2]