// test lifetime inference with lifetime overloading but same types

method m<&a>(&a:int x, &a:int y) -> int:
    return 1

method m<&a>(&a:int x, &*:int y) -> int:
    return 2

method m<&a>(&*:int x, &a:int y) -> int:
    return 3

public export method test():
    // variant1: (&*:int, &*:int) <--
    // variant2: (&*:int, &*:int) <--
    // variant3: (&*:int, &*:int) <--
    m(new 1, new 2)
