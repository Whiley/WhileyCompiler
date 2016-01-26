

method f(int[] x) :
    int z = |x|
    assume x[z - 1] == 3

public export method test() :
    int[] arr = [1, 2, 3]
    f(arr)
