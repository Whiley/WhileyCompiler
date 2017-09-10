type fun_t is function(int)->(int)
type meth_t is method(int)->(int)

function f(int x) -> (int y):
    return 0

method m(int x) -> (int y):
    return 0

method test2():
    //    
    meth_t x = &f
    //
    fun_t y = &m
