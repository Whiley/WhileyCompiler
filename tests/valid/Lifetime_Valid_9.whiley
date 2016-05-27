// use lifetime arguments to disambiguate (see also Lifetime_Invalid_8)

method <a, b> m((&a:int)|(&b:bool) x, (&a:int)|(&b:int) y) -> &a:int:
    return a:new 1

public export method test():
    // <this, *> --> ((&this:int)|(&*:bool), &this:int)
    // <*, this> --> ((&*:int)|(&this:bool), &this:int)
    (&*:int)|(&*:bool) x = *:new 1
    &this:int y = this:new 1

    &this:int p1 = m<this, *>(x, y)
    &*:int p2 = m<*, this>(x, y)
    &this:int p3 = m<this, this>(x, y)
