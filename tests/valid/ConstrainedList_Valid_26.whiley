

function f(int[] ls) -> (int[] r)
ensures r == [0;0]:
    //
    if |ls| == 0:
        return ls
    else:
        return [0;0]

public export method test() :
    int[] items = [5, 4, 6, 3, 7, 2, 8, 1]
    assume f(items) == [0;0]
    assume f([0;0]) == [0;0]
