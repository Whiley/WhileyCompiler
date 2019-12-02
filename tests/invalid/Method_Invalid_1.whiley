type meth_t is method()

method m() :
    int b = 1

public export method test():
    meth_t fn = &m    
    int a1 = fn()
    int a2 = fn()+1
    int[] a3 = [fn()]
