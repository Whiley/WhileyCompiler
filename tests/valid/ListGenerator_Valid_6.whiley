function f() -> (bool[] ys):
    return [false; 3]

public export method test():
    assume f() == [false,false,false]
