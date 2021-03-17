type arr is (int[] xs) where all { i in 0..|xs| | xs[i] >= 0 }

public export method test():
    arr a = [0,1,2]
    a[0] = -1
    //
    assume a == [-1,1,2]
