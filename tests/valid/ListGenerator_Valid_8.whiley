function f() -> (bool[] ys):
    return [false; 2]

public export method test():
    assume f() == [false,false]
