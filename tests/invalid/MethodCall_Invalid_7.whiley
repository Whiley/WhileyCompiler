type wmccf7tup is {int y, int x}

function f(int y) -> wmccf7tup:
    return {y: get(), x: 1}

method get() -> int:
    return 1
