// test lifetime inference with lifetime overloading and different types

method <a, b> m((&a:int)|(&b:bool) x, &a:int y, bool z) -> &b:int:
    return b:new 1

method <a> m(&a:bool x, &a:int y, bool z) -> &a:int:
    return a:new 2

method <a> m(&*:int x, &a:int y, int|bool z) -> &a:int:
    return a:new 3

method <a> m(&*:int x, &a:int y, bool z) -> &a:int:
    return a:new 4

public export method test():
    // variant1: <this, *> ((&this:int)|(&*:bool), &this:int, bool z)
    // variant2: no
    // variant3: no
    // variant4: no
    &*:int p1 = m(this:new 1, this:new 2, true)
    assume (*p1) == 1

    // variant1: <this, *> ((&this:int)|(&*:bool), &this:int, bool z)
    // variant2: no
    // variant3: <this> (&*:int, &this:int, int|bool)
    // variant4: <this> (&*:int, &this:int, bool) <--
    &this:int p2 = m(*:new 1, this:new 2, true)
    assume (*p2) == 4
