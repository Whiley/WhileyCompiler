function f() -> (real[] ys):
    return [1.0; 2]

public export method test():
    assume f() == [1.0,1.0]
