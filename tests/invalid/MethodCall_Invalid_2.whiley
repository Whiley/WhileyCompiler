type dummy is &{int x}

method f(dummy this, int x) -> int:
    return 1

method main(&int this) :
    f(this, 1)
