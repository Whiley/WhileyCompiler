type wmccf6tup is {int y, int x}

function f(int y) -> wmccf6tup:
    return {y: get(), x: 1}

method get() -> int:
    return 1
