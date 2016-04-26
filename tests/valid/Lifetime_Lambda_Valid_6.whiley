// Test co-variant return types

public export method test():
    method<a>(&a:int)->(&a:int) m1 = &<b>(&b:int x -> new 1)
    &int xx = m1(new 2)
    assume (*xx) == 1
    method<a, b>(&a:int)->(&a:int|&b:bool) m2 = &<b, c>(&b:int x -> c:new true)
    c:
        d:
            &this:bool yy = m2<c, *>(c:new 1)
            assume (*yy) == true
