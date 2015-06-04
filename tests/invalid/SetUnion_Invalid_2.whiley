function f({int} xs) -> int:
    return |xs|

method main() -> int:
    {real} ys = {1.0234234, 1.12}
    {int} xs = {1, 2, 3, 4}
    return f(xs + ys)
