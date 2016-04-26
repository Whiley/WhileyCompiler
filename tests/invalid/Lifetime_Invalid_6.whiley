// test lifetime inference with lifetime overloading but same types

method <a> m(&a:int x, &a:int y) -> int:
    return 1

method <a, b> m(&a:int x, &b:int y) -> int:
    return 2

public export method test():
    // variant1: (&this:int, &this:int) <--
    // variant2: (&this:int, &this:int) <--
    m(this:new 1, this:new 2)
