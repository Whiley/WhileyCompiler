method <a,b> f(&a:int p, &a:int q, &b:int r) -> (&b:int s):
    return r

method g():
    &this:int x = new 1
    &this:int y = new 2
    &this:int z = new 3
    &this:int w = f(x,y,z)
    //
    assume z == w

method <l> h(&l:int p) -> (&l:int r):
    &this:int q = new 1
    // this, l
    return f(p,q,p)

public export method test():
    &this:int x = new 1
    &this:int y = h<this>(x)
    assume x == y
    //
    g()
