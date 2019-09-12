// test lifetime inference with lifetime overloading and different types

method m<&a,&b>((&a:int)|(&b:bool) x, (&a:int)|(&b:int) y):
    skip

method main(&int|&bool n):
    inner:
        (&inner:int)|(&*:bool) x = n
        &this:int y = this:new 1
        m(x, y)

public export method test():
    main(*:new 1)