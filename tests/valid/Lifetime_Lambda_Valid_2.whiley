// Test that lifetime parameters are replaced without capturing nested parameters in return types

type meth1 is method<a,b>(&a:int, &b:int)->(&b:meth2)
type meth2 is method<a,b>(&a:int, &b:int)->(int)

public export method test():
    meth2 m2 = &<aa, bb>(&aa:int x, &bb:int y -> *(new 1))
    meth1 m1 = &<bb, aa>(&bb:int x, &aa:int y -> aa:new m2)
    &*:meth2 m3 = m1<this, *>(this:new 2, new 3)
