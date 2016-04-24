// test lifetime inference with lifetime overloading but same types

method <a> m(&a:int x, &a:int y) -> int:
    return 1

method <a> m(&a:int x, &*:int y) -> int:
    return 2

method <a> m(&*:int x, &a:int y) -> int:
    return 3

method <a> m2(&a:int x, &a:int y) -> int:
    return 4

method <a, b> m2(&a:int x, &b:int y) -> int:
    return 5

public export method test():
    test:
        &this:int ptr1 = this:new 1
        &test:int ptr2 = test:new 2
        &*:int ptr3 = *:new 2

        // variant1: (&test:int, &test:int) <--
        // variant2: no
        // variant3: no
        int r = m(ptr1, ptr2)
        assume r == 1

        // variant1: (&this:int, &this:int)
        // variant2: no
        // variant3: (&*:int, &this:int) <--
        r = m(ptr3, ptr1)
        assume r == 3

        // variant1: (&test:int, &test:int)
        // variant2: (&test:int, &*:int) <--
        // variant3: no
        r = m(ptr2, ptr3)
        assume r == 2

        // variant4: (&test:int, &test:int)
        // variant5: (&test:int, &this:int) <--
        r = m2(ptr2, ptr3)
        assume r == 5
