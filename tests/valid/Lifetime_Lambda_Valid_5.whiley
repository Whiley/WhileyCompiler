// Test contra-variant parameter types

public export method test():
    method<a>(&a:int, &int)->(&int) m1 = &<b>(&b:int x, &b:int y -> new 1)
    method<a>(&a:int, &int)->(&int) m2 = &<b>(&b:int x, &b:int y -> new 1)
    method<a, b>(&a:int, &b:int)->(&a:int) m3 = &<b, a>(&b:int x, &a:int y -> b:new 1)
