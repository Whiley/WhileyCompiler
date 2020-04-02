type nat is (int x) where x >= 0

function loop(int[] array, int n) -> int
requires |array| > 0:
    //
    nat i = next(array)
    //
    while array[i] != 0 && n >= 0
    where i < |array|:
        i = next(array)
        n = n - 1
    //
    return i

function next(int[] arr) -> (nat r)
requires |arr| > 0
ensures r >= 0 && r < |arr|:
    //
    return 0

public export method test():
    int[] A = [1,2,3]
    assume loop(A,5) == 0

