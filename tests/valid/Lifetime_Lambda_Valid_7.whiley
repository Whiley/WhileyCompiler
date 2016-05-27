// Test context lifetimes

public export method test():
    &this:int x = this:new 2
    function()->(&this:int) m1 = &(->x)
    int i = *m1()
    assume i == 2

    method[this]()->(int) m2 = &[this](->*x)
    i = m2()
    assume i == 2
    *x = 3
    i = m2()
    assume i == 3

    a:
        &a:int y = a:new 4
        method[this,a]()->(int) m3 = &[this](->*x)
        i = m3()
        assume i == 3
        m3 = &[this,a](->*x)
        i = m3()
        assume i == 3
        method[this,a]()->(int) m4 = &[this,a](->*y)
        i = m4()
        assume i == 4
