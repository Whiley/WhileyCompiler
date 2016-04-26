// test that substitution does not accidentaly substitute already substituted values

method <a, b> m(&a:int x, &b:int y) -> &a:int:
    return x

public export method test():
    a:
        b:
            &a:int x = a:new 1
            &b:int y = b:new 2
            &a:int z = m(x, y)
            assume x == z
