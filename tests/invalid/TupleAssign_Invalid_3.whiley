
type tac3ta is ({int f1, int f2} _this) where _this.f1 < _this.f2

method main():
    {int f1, int f2} x = {f1: 2, f2: 3}
    tac3ta y = {f1: 1, f2: 3}
    x.f1 = 1
    assert x != y
