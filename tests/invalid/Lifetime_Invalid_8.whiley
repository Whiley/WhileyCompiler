// test lifetime inference with lifetime overloading and different types

method <a, b> m((&a:int)|(&b:bool) x, (&a:int)|(&b:int) y):

public export method test():
    // <this, *> --> ((&this:int)|(&*:bool), &this:int)
    // <*, this> --> ((&*:int)|(&this:bool), &this:int)
    (&*:int)|(&*:bool) x = *:new 1
    &this:int y = this:new 1
    m(x, y)
