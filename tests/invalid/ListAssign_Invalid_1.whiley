function f([int] ls) -> [int]:
    return ls

method main():
    [int] xs = [1, 2]
    xs[0] = 1.23
    f(xs)
