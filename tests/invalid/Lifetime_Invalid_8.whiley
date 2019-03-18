// test lifetime inference with lifetime overloading and different types

method m<&a,&b>((&a:int)|(&b:bool) x, (&a:int)|(&b:int) y):
    skip

public export method test():
    inner:
        (&inner:int)|(&*:bool) x = *:new 1
        &this:int y = this:new 1
        m(x, y)
