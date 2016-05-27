type myfun is function(int)->(int)

public export method test():
    myfun f = &(int x -> x)
    int x = f(1)
    assume x == 1
