function f() -> (int[][] ys):
    return [[0]; 1]

public export method test():
    assume f() == [[0]]
