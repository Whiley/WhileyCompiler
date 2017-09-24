type dummy is &{int x}

method f(dummy _this, int x) -> int:
    return 1

public export method test(&int _this) :
    f(_this, 1)
