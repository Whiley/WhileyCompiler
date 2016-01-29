

public export method test() :
    {int f1, int f2} x = {f1: 2, f2: 3}
    {int f1, int f2} y = {f1: 1, f2: 3}
    assert x != y
    x.f1 = 1
    assume x == {f1: 1, f2: 3}
    assume y == {f1: 1, f2: 3}
    assert x == y
