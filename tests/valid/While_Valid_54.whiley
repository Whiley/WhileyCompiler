function permute(int[] xs) -> (int[] rs, int[] witness)
ensures |rs| == |witness|
ensures |xs| == |witness|
ensures all { k in 0..|witness| | xs[k] == rs[witness[k]] }:
    //
    int i = 0
    int[] witness = [0; |xs|]
    //
    while i < |xs|
    where i >= 0 && |xs| == |witness|
    where all { j in 0..i | xs[j] == xs[witness[j]] }:
        witness[i] = i
        i = i + 1
    //
    return xs,witness

method test():
    assume permute([1,2,3]) == [1,2,3],[0,1,2]