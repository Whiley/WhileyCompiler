

type wmcr6tup is {int y, int x}

method get() -> int:
    return 1

method f(int y) -> wmcr6tup:
    return {y: get(), x: y}

public export method test() :
    assume f(2) == {y: 1, x: 2}
