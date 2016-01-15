

function update(int[][] ls) -> int[][]
requires |ls| > 0 && |ls[0]| > 0:
    //
    ls[0][0] = 10
    return ls

function f(int[][] ls) -> {int[][] f1, int[][] f2}
requires |ls| > 0 && |ls[0]| > 0:
    //
    int[][] nls = update(ls)
    return {f1: ls, f2: nls}

public export method test() :
    int[][] ls = [[1, 2, 3, 4]]
    {int[][] f1, int[][] f2} r = f(ls)
    assume r.f1 == [[1,2,3,4]]
    assume r.f2 == [[10,2,3,4]]    
