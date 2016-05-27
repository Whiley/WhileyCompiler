type dummy is &{int x}

method f(dummy _this, int x) -> int:
    return 1

method main(&int _this) :
    f(_this, 1)
