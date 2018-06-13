public export method test():
    int[] a = [0,1]
    assume some { i in 0..|a| | a[i] == 0 && all { j in 0..|a| | a[j] == 0 <==>  i == j } }
