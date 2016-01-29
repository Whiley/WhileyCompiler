

type nat is (int x) where x >= 0

function get(nat[] ls, int i) -> (int r)
requires (i >= 0) && (i <= |ls|)
ensures r >= 0:
    //
    if i == |ls|:
        return 0
    else:
        return ls[i]

public export method test() :
    int[] xs = [1, 3, 5, 7, 9, 11]
    assume get(xs, 0) == 1
    assume get(xs, 1) == 3
    assume get(xs, 2) == 5
    assume get(xs, 3) == 7
    assume get(xs, 4) == 9
    assume get(xs, 5) == 11
