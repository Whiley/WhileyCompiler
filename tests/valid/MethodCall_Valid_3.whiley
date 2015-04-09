import whiley.lang.*

type wmcr6tup is {int y, int x}

method get() -> int:
    return 1

method f(int y) -> wmcr6tup:
    return {y: get(), x: y}

method main(System.Console sys) -> void:
    assume f(2) == {y: 1, x: 2}
