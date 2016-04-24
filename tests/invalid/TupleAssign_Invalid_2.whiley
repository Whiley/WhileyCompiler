
type tac2ta is ({int f1, int f2} _this) where _this.f1 < _this.f2

type tac2tb is ({int f1, int f2} _this) where (_this.f1 + 1) < _this.f2

function f(tac2tb y) -> tac2tb:
    return y

method main() :
    tac2ta x = {f1: 1, f2: 3}
    x.f1 = 2
    f(x)
