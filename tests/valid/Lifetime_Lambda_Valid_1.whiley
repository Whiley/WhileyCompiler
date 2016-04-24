type meth is method<a, b>(&a:int, &b:int)->(&a:int)

public export method test():
    meth m = &<a, b>(&a:int x, &b:int y -> a:new 1)
    m = &<a, b>(&a:int x, &b:int y -> new 1)
