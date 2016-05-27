
type tac1tup is ({int f1, int f2} _this) where _this.f1 < _this.f2

method main() :
    tac1tup x = {f1: 1, f2: 3}
    x.f1 = 2
    assert x.f1 == x.f2
