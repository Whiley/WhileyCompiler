type fun_t is function(int)->(int)
type gun_t is function(any)->(int)

function f(any x) -> (int y):
    return 0

function g(int x) -> (int y):
    return 0

method test():
    //    
    fun_t x = &f
    //
    fun_t y = &g
    //
    gun_t z = &g
    