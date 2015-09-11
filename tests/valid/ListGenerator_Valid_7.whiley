function f() -> (int[] ys):
    return [0; 4]

public export method test():
    assume f() == [0,0,0,0]
