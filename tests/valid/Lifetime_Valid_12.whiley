method f<&a,&b>(&a:int q, &b:int r) -> (&b:int s):
    return r

method h<&l>(&l:int p) -> (&l:int r):
    &this:int q = new 1
    return f(q,p)
    
public export method test():
    &this:int x = new 1
    &this:int y = h(x)
    assume x == y
