// use lifetime arguments to disambiguate (see also Lifetime_Invalid_8)

method m<&a,&b>((&a:int)|(&b:bool) x, (&a:int)|(&b:byte) y) -> &a:int:
    return a:new 1

public export method test():
    // <this, *> --> ((&this:int)|(&*:bool), &this:int)
    // <*, this> --> ((&*:int)|(&this:bool), &this:int)
    (&*:int)|(&*:bool) x = *:new 1
    &this:int y = this:new 0
    &this:byte z = this:new 0b00_1101

    &this:int p1 = m<this, *>(x, y)
    &*:int p2 = m<*, this>(x, z)
    &this:int p3 = m<this, this>(x, z)
