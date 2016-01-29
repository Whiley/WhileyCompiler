type nint is null | int

function move(int from, int to, nint[][] list) -> nint[][]
requires from >= 0 && from < |list|
requires from + 1 < |list[from]|
requires to >= 0 && to < |list|
requires to + 1 < |list[to]|:
    //
    nint tmp = list[from][from + 1]
    list[from][from + 1] = null
    list[to][to + 1] = tmp
    return list

public export method test() :
    nint[][] ls = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
    ls = move(0, 1, ls)
    assume ls == [[1, null, 3], [4, 5, 2], [7, 8, 9]]
