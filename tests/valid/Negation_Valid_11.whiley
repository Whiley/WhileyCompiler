type intrec is {int i}

public export method test():
    intrec r = {i: 1}
    assume -(r.i) == -1
    assume -1 == -(r.i)
    int[] a = [-1]
    assume -1 == a[0]
    assume a[0] == -1
    assume -([-1][0]) == 1