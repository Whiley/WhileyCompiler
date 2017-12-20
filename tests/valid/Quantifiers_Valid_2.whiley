public export method test():
    int[] a = [0,1]
    assert some { i in 0..|a| | all { j in 0..|a| | a[j] == 0 <==>  i == j } }