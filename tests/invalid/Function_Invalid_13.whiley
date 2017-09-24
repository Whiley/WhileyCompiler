type fun_t is function(int)->(any)
type gun_t is function(int)->(int)

function f(int x) -> (int y):
    return 0

function g(int x) -> (any y):
    return 0

method test():
    //    
    fun_t x = &f
    //
    fun_t y = &g
    //
    gun_t z = &g
