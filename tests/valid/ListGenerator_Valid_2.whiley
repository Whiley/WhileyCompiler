

method f(int[] x)
requires |x| > 0:
    int z = |x|
    assume x[z - 1] == 3

public export method test() :
    int[] arr = [1, 2, 3]
    f(arr)
